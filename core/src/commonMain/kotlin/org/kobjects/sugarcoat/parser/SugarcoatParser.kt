package org.kobjects.sugarcoat.parser

import org.kobjects.parsek.tokenizer.Scanner
import org.kobjects.sugarcoat.ast.Definition
import org.kobjects.sugarcoat.ast.FunctionDefinition
import org.kobjects.sugarcoat.ast.ParameterDefinition
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.FieldDefinition
import org.kobjects.sugarcoat.ast.ImplDefinition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.Program
import org.kobjects.sugarcoat.ast.StructDefinition
import org.kobjects.sugarcoat.ast.SymbolExpression
import org.kobjects.sugarcoat.ast.TraitDefinition
import org.kobjects.sugarcoat.ast.Type
import org.kobjects.sugarcoat.ast.TypeReference
import org.kobjects.sugarcoat.ast.VariableDeclaration
import org.kobjects.sugarcoat.datatype.VoidType

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

    private fun parseProgram(scanner: Scanner<TokenType>): Program {
        val program = Program()
        val parsingContext = ParsingContext(program)
        while (scanner.current.type != TokenType.EOF) {
            if (scanner.current.type == TokenType.NEWLINE) {
                scanner.require(currentIndent(scanner) == 0) { "Unexpected indent: ${currentIndent(scanner)}." }
                scanner.consume()
            } else {
                when (scanner.current.text) {
                    "fn" -> parseFn(scanner, parsingContext, VoidType)
                    "impl" -> parseImpl(scanner, parsingContext)
                    "struct" -> parseStruct(scanner, parsingContext)
                    "trait" -> parseTrait(scanner, parsingContext)
                    else -> throw scanner.exception("Unexpected token.")
                }
            }
        }
        require(program.definitions["main"] is FunctionDefinition) {
            "main() function not found."
        }
        return program
    }

    fun parseFn(scanner: Scanner<TokenType>, parentContext: ParsingContext, receiverType: Type) {
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
        val body = parseBlock(scanner, parentContext)
        val fn = FunctionDefinition(receiverType, parameters, returnType, body)
        parentContext.definition.addDefinition(name, fn)
    }


    fun parseImpl(scanner: Scanner<TokenType>, parentContext: ParsingContext) {
        scanner.consume("impl")
        val trait = parseType(scanner, parentContext)
        scanner.consume("for")
        val struct = parseType(scanner, parentContext)

        val impl = ImplDefinition(trait, struct)

        parseStructLike(scanner, parentContext, impl)
        parentContext.definition.addDefinition("$trait for $struct", impl)
    }


    fun parseStruct(scanner: Scanner<TokenType>, parentContext: ParsingContext) {
        scanner.consume("struct")
        val name = scanner.consume(TokenType.IDENTIFIER) { "Identifier expected after 'struct'." }.text
        val struct = StructDefinition()
        parseStructLike(scanner, parentContext, struct)
        parentContext.definition.addDefinition(name, struct)
    }

    fun parseTrait(scanner: Scanner<TokenType>, parentContext: ParsingContext) {
        scanner.consume("trait")
        val name = scanner.consume(TokenType.IDENTIFIER) { "Identifier expected after 'trait'." }.text
        val trait = TraitDefinition()
        parseStructLike(scanner, parentContext, trait)
        parentContext.definition.addDefinition(name, trait)
    }

    fun parseStructLike(
        scanner: Scanner<TokenType>,
        parentContext: ParsingContext,
        definition: Definition
    ) {
        val depth = currentIndent(scanner)
        if (depth <= parentContext.depth) {
            return
        }
        val parsingContext = parentContext.copy(depth = depth, definition = definition)
        scanner.consume(TokenType.NEWLINE)
        while (true) {
            if (scanner.current.type != TokenType.NEWLINE) {
                val isStatic =  scanner.tryConsume("static")

                if (scanner.current.text == "fn") {
                    parseFn(scanner, parsingContext, definition as Type)
                } else {
                    val name = scanner.consume(TokenType.IDENTIFIER) { "Field name expected" }.text
                    scanner.consume(":") { "Colon separating field name and type expected." }
                    val type = parseType(scanner, parsingContext)
                    definition.addDefinition(name, FieldDefinition(type))
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
            return SymbolExpression("seq")
        }
        scanner.consume(TokenType.NEWLINE)

        val parsingContext = parentContext.copy(depth = depth)

        val result = mutableListOf<ParameterReference>()
        while(true) {
            println("parsebody loop parsing at depth $depth")
            if (scanner.current.type != TokenType.NEWLINE) {
                val statement = parseStatement(scanner, parsingContext)
                println("parsed @$depth: $statement")
                result.add(ParameterReference("", statement))
            }
            if (currentIndent(scanner) != depth) {
                println("leaving b/c currentDepth = ${currentIndent(scanner)} != $depth")
                break
            }
            scanner.consume(TokenType.NEWLINE)
        }
        return if (result.size == 1) result.first().value else SymbolExpression(null, "seq", result)
    }

    fun parseStatement(scanner: Scanner<TokenType>, parsingContext: ParsingContext): Expression {
        return if (scanner.tryConsume("let")) {
            parseVariableDeclaration(scanner, parsingContext)
        } else {
            var result = parseExpression(scanner, parsingContext)
            if (scanner.tryConsume("=")) {
                throw RuntimeException("TBD")
            }
            result
        }
    }

    fun parseType(scanner: Scanner<TokenType>, parsingContext: ParsingContext): Type {
        val name = (scanner.consume(TokenType.IDENTIFIER) { "Type name identifier expected." }).text
        return TypeReference(name)
    }


    fun parseVariableDeclaration(scanner: Scanner<TokenType>, parsingContext: ParsingContext): Expression {
        val name = scanner.consume(TokenType.IDENTIFIER).text
        scanner.consume("=")
        val value = parseExpression(scanner, parsingContext)
        return VariableDeclaration(name, value)
    }

        fun parseProgram(code: String) =
            parseProgram(Scanner(NewlineFilter(SugarcoatLexer(code)), TokenType.EOF))

}