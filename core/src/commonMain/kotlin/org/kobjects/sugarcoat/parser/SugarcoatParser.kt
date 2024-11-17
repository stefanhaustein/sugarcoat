package org.kobjects.sugarcoat.parser

import org.kobjects.parsek.tokenizer.Scanner
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.model.ImplDefinition
import org.kobjects.sugarcoat.model.ObjectDefinition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.model.Program
import org.kobjects.sugarcoat.model.StructDefinition
import org.kobjects.sugarcoat.ast.UnresolvedSymbolExpression
import org.kobjects.sugarcoat.model.TraitDefinition
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.type.UnresolvedTypeReference
import org.kobjects.sugarcoat.ast.VariableDeclaration
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.DelegateToImpl

object SugarcoatParser {

    internal fun parseExpression(scanner: Scanner<TokenType>, parsingContext: ParsingContext): Expression =
        ExpressionParser.parseExpression(scanner, parsingContext)

    internal fun currentIndent(scanner: Scanner<TokenType>): Int {
        if (scanner.current.type == TokenType.EOF) {
            return 0
        }
        scanner.require(scanner.current.type == TokenType.NEWLINE) { "Newline expected." }
        val newlinePos = scanner.current.text.lastIndexOf('\n')
        return scanner.current.text.length - newlinePos - 1
    }

    private fun parseProgram(scanner: Scanner<TokenType>, printFn: (Any) -> Unit = ::print): Program {
        val program = Program(printFn)
        val parsingContext = ParsingContext(program)
        while (scanner.current.type != TokenType.EOF) {
            if (scanner.current.type == TokenType.NEWLINE) {
                scanner.require(currentIndent(scanner) == 0) { "Unexpected indent: ${currentIndent(scanner)}." }
                scanner.consume()
            } else {
                when (scanner.current.text) {
                    "fn" -> parseFn(scanner, parsingContext, true)
                    "impl" -> parseImpl(scanner, parsingContext)
                    "object" -> parseObject(scanner, parsingContext)
                    "struct" -> parseStruct(scanner, parsingContext)
                    "trait" -> parseTrait(scanner, parsingContext)
                    else -> throw scanner.exception("Unexpected token.")
                }
            }
        }
        require(program.definitions["main"] is FunctionDefinition) {
            "main() function not found."
        }
        program.resolveAll()
        return program
    }

    fun parseFn(scanner: Scanner<TokenType>, parentContext: ParsingContext, static: Boolean) {
        scanner.consume("fn")
        val name = scanner.consume(TokenType.IDENTIFIER) { "Identifier expected after 'fn'." }.text
        scanner.consume("(") { "Opening brace expected after function name '$name'." }
        val parameters = mutableListOf<ParameterDefinition>()
        if (!scanner.tryConsume(")")) {
            do {
                val name = scanner.consume(TokenType.IDENTIFIER) { "Parameter name expected." }.text
                scanner.consume(":") { "Colon separating parameter name and type expected." }
                val type = parseType(scanner, parentContext)
                parameters.add(ParameterDefinition(name, type))
            } while (scanner.tryConsume(","))
            scanner.consume(")") { "Closing brace or comma (')' or ',') expected after parameter" }
        }
        val returnType = if (scanner.tryConsume("->")) parseType(scanner, parentContext) else VoidType

        val fn: Classifier = if (!static && parentContext.namespace is TraitDefinition) {
            DelegateToImpl(
                parentContext.namespace,
                parentContext.namespace,
                name,
                parameters,
                returnType
            )
        } else {
            val fd = FunctionDefinition(
                scanner.position(),
                parentContext.namespace,
                parentContext.namespace,
                static,
                name,
                parameters,
                returnType
            )
            fd.body = parseBlock(scanner, parentContext.copy(namespace = fd))
            fd
        }
        parentContext.namespace.addChild(fn)
    }


    fun parseImpl(scanner: Scanner<TokenType>, parentContext: ParsingContext) {
        scanner.consume("impl")
        val trait = parseType(scanner, parentContext)
        scanner.consume("for")
        val struct = parseType(scanner, parentContext)

        val impl = ImplDefinition(parentContext.namespace, parentContext.program, trait, struct)

        parseClassifier(scanner, parentContext, impl)
        parentContext.namespace.addChild(impl)
    }


    fun parseStruct(scanner: Scanner<TokenType>, parentContext: ParsingContext) {
        scanner.consume("struct")
        val name = scanner.consume(TokenType.IDENTIFIER) { "Identifier expected after 'struct'." }.text
        val constructorName = if (scanner.tryConsume("constructor")) scanner.consume(TokenType.IDENTIFIER).text else "create"
        val struct = StructDefinition(parentContext.namespace, parentContext.program, name, constructorName)
        parseClassifier(scanner, parentContext, struct)
        parentContext.namespace.addChild(struct)
    }


    fun parseObject(scanner: Scanner<TokenType>, parentContext: ParsingContext) {
        scanner.consume("object")
        val name = scanner.consume(TokenType.IDENTIFIER) { "Identifier expected after 'object'." }.text
        val o = ObjectDefinition(parentContext.namespace, parentContext.program, name)
        parseClassifier(scanner, parentContext, o)
        parentContext.namespace.addChild(o)
    }

    fun parseTrait(scanner: Scanner<TokenType>, parentContext: ParsingContext) {
        scanner.consume("trait")
        val name = scanner.consume(TokenType.IDENTIFIER) { "Identifier expected after 'trait'." }.text
        val trait = TraitDefinition(parentContext.namespace, parentContext.program, name)
        parseClassifier(scanner, parentContext, trait)
        parentContext.namespace.addChild(trait)
    }

    fun parseClassifier(
        scanner: Scanner<TokenType>,
        parentContext: ParsingContext,
        classifier: Classifier
    ) {
        val depth = currentIndent(scanner)
        if (depth <= parentContext.depth) {
            return
        }
        val parsingContext = parentContext.copy(depth = depth, namespace = classifier)
        scanner.consume(TokenType.NEWLINE)
        while (true) {
            if (scanner.current.type != TokenType.NEWLINE) {
                val static =  scanner.tryConsume("static") || classifier is ObjectDefinition || classifier is Program

                if (scanner.current.text == "fn") {
                    parseFn(scanner, parsingContext, static)
                } else {
                    val mutable = scanner.tryConsume("mut")

                    val name = scanner.consume(TokenType.IDENTIFIER) { "Field name expected" }.text
                    var type: Type? = null
                    if (scanner.tryConsume(":")) {
                        type = parseType(scanner, parsingContext)
                    }
                    val valueExpr = if (scanner.tryConsume("=")) parseExpression(scanner, parsingContext) else null
                    if (static) {
                        require (valueExpr != null) {
                            "${scanner.position()}: Initializer expression required for static fields."
                        }
                        classifier.addStaticField(mutable, name, type, valueExpr)
                    } else {
                        require (type != null) {
                            "${scanner.position()}: Type required for instance fields."
                        }
                        classifier.addInstanceField(mutable, name, type, valueExpr)
                    }
                }
            }
            if (currentIndent(scanner) != depth) {
                println("leaving b/c currentDepth = ${currentIndent(scanner)} != $depth")
                break
            }
            scanner.consume(TokenType.NEWLINE)
        }
    }


    fun parseBlock(scanner: Scanner<TokenType>, parentContext: ParsingContext): Expression {
        val depth = currentIndent(scanner)
        println("ParseBody; parent: $parentContext; depth: $depth")
        if (depth <= parentContext.depth) {
            return LiteralExpression(scanner.position(), Unit)
        }
        scanner.consume(TokenType.NEWLINE)

        val parsingContext = parentContext.copy(depth = depth)

        val result = mutableListOf<Expression>()
        while(true) {
            println("parsebody loop parsing at depth $depth")
            if (scanner.current.type != TokenType.NEWLINE) {
                val statement = parseStatement(scanner, parsingContext)
                println("parsed @$depth: $statement")
                result.add(statement)
            }
            if (currentIndent(scanner) != depth) {
                println("leaving b/c currentDepth = ${currentIndent(scanner)} != $depth")
                break
            }
            scanner.consume(TokenType.NEWLINE)
        }
        return when (result.size) {
            0 -> LiteralExpression(scanner.position(), Unit)
            1 -> result.first()
            else -> UnresolvedSymbolExpression(
                scanner.position(),
                null,
                "seq",
                result.mapIndexed { index, expr ->
                    ParameterReference(if (index == result.size - 1) "result" else "", expr)
                })
        }

//                (result.size == 1) result.first().value
  //      else UnresolvedSymbolExpression(scanner.position(), null, "seq", result)
    }

    fun parseStatement(scanner: Scanner<TokenType>, parsingContext: ParsingContext): Expression {
        return if (scanner.tryConsume("let")) {
            parseVariableDeclaration(scanner, parsingContext)
        } else {
            var result = parseExpression(scanner, parsingContext)
            if (scanner.tryConsume("=")) {
                require (result is UnresolvedSymbolExpression && result.children.isEmpty()) {
                    "Unsupported assignment target: $result"
                }
                val source = parseExpression(scanner, parsingContext)
                result = UnresolvedSymbolExpression(scanner.position(), result.receiver,"set_${result.name}", listOf(ParameterReference("", source)))
            }
            result
        }
    }

    fun parseType(scanner: Scanner<TokenType>, parsingContext: ParsingContext): Type {
        var name = (scanner.consume(TokenType.IDENTIFIER) { "Type name identifier expected." }).text
        while (scanner.current.type == TokenType.PROPERTY) {
            name += scanner.consume().text
        }

        val genericParameters = mutableListOf<Type>()
        if (scanner.tryConsume("<")) {
            do {
                genericParameters.add(parseType(scanner, parsingContext))
            } while (scanner.tryConsume(","))
            scanner.consume(">") { "'>' expected at the end of the generic parameter name list." }
        }
        return UnresolvedTypeReference(scanner.position(), name, genericParameters.toList())
    }


    fun parseVariableDeclaration(scanner: Scanner<TokenType>, parsingContext: ParsingContext): Expression {
        val mutable = scanner.tryConsume("mut")
        val name = scanner.consume(TokenType.IDENTIFIER).text
        var explicitType: Type? = null
        if (scanner.tryConsume(":")) {
            explicitType = parseType(scanner, parsingContext)
        }
        scanner.consume("=")
        val value = parseExpression(scanner, parsingContext)
        //parsingContext.namespace.addField(name, explicitType, value)
        return VariableDeclaration(scanner.position(), name, mutable, explicitType, value)
    }

        fun parseProgram(code: String, printFn: (Any) -> Unit = ::print) =
            parseProgram(Scanner(NewlineFilter(SugarcoatLexer(code)), TokenType.EOF), printFn)

}