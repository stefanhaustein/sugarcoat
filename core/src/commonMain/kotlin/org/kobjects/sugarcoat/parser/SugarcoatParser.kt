package org.kobjects.sugarcoat.parser

import org.kobjects.parsek.tokenizer.Scanner
import org.kobjects.sugarcoat.ast.Callable
import org.kobjects.sugarcoat.ast.FunctionDefinition
import org.kobjects.sugarcoat.ast.ParameterDefinition
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.Program
import org.kobjects.sugarcoat.ast.StructDefinition
import org.kobjects.sugarcoat.ast.SymbolExpression
import org.kobjects.sugarcoat.ast.Type
import org.kobjects.sugarcoat.ast.TypeReference
import org.kobjects.sugarcoat.ast.VariableDeclaration
import org.kobjects.sugarcoat.datatype.VoidType

class SugarcoatParser internal constructor(val scanner: Scanner<TokenType>) {
    val program = Program()

    internal fun parseExpression(depth: Int): Expression = ExpressionParser.parseExpression(scanner, ParsingContext(depth, this))

    internal fun currentIndent(): Int {
        if (scanner.current.type == TokenType.EOF) {
            return 0
        }
        scanner.require(scanner.current.type == TokenType.NEWLINE) { "Newline expected." }
        val newlinePos = scanner.current.text.lastIndexOf('\n')
        return scanner.current.text.length - newlinePos - 1
    }

    private fun parseProgram(): Program {
        while (scanner.current.type != TokenType.EOF) {
            if (scanner.current.type == TokenType.NEWLINE) {
                scanner.require(currentIndent() == 0) { "Unexpected indent: ${currentIndent()}." }
                scanner.consume()
            } else {
                when (scanner.current.text) {
                    "fn" -> {
                        val f = parseFn(0, null)
                        program.addFunction(f.first, f.second)
                    }
                    "struct" -> {
                        val s = parseStruct(0)
                        program.addType(s.first, s.second)
                    }
                    else -> throw scanner.exception("Unexpected token.")
                }
            }
        }
        require(program.functions.containsKey("main")) {
            "main() function not found."
        }
        return program
    }

    fun parseFn(parentDepth: Int, receiverType: Type?): Pair<String, FunctionDefinition> {
        scanner.consume("fn")
        val name = scanner.consume(TokenType.IDENTIFIER) { "Identifier expected after 'fn'." }.text
        scanner.consume("(") { "Opening brace expected after function name '$name'." }
        val parameters = mutableListOf<ParameterDefinition>()
        if (!scanner.tryConsume(")")) {
            do {
                val name = scanner.consume(TokenType.IDENTIFIER) { "Parameter name expected." }.text
                scanner.consume(":") { "Colon separating parameter name and type expected." }
                val type = parseType(parentDepth)
                parameters.add(ParameterDefinition(name, type))
            } while (scanner.tryConsume(","))
            scanner.consume(")") { "Closing brace or comma (')' or ',') expected after parameter" }
        }
        val returnType = if (scanner.tryConsume("->")) parseType(parentDepth) else VoidType
        scanner.consume(":") { "Colon expected after function parameter list." }
        val body = parseBody(parentDepth)
        val fn = FunctionDefinition(receiverType, parameters, returnType, body)
        return name to fn
    }

    fun parseStruct(parentDepth: Int): Pair<String, StructDefinition> {
        scanner.consume("struct")
        val name = scanner.consume(TokenType.IDENTIFIER) { "Identifier expected after 'struct'." }.text
        scanner.consume(":") { "Colon expected after function parameter list." }
        val struct = StructDefinition()
        val depth = currentIndent()
        if (depth <= parentDepth) {
            return name to struct
        }
        scanner.consume(TokenType.NEWLINE)
        while (true) {
            if (scanner.current.type != TokenType.NEWLINE) {
                if (scanner.current.text == "fn") {
                    parseFn(depth, struct)
                } else {
                    val name = scanner.consume(TokenType.IDENTIFIER) { "Field name expected" }.text
                    scanner.consume(":") { "Colon separating field name and type expected." }
                    val type = parseType(depth)
                    struct.addField(name, type)
                }
            }
            if (currentIndent() != depth) {
                println("leaving b/c currentDepth = ${currentIndent()} != $depth")
                break
            }
            scanner.consume(TokenType.NEWLINE)
        }
        return name to struct
    }

    fun parseBody(parentDepth: Int): Expression {
        val depth = currentIndent()
        println("ParseBody; parentDepth: $parentDepth; depth: $depth")
        if (depth <= parentDepth) {
            return SymbolExpression("seq")
        }
        scanner.consume(TokenType.NEWLINE)
        val result = mutableListOf<ParameterReference>()
        while(true) {
            println("parsebody loop parsing at depth $depth")
            if (scanner.current.type != TokenType.NEWLINE) {
                val statement = parseStatement(depth)
                println("parsed @$depth: $statement")
                result.add(ParameterReference("", statement))
            }
            if (currentIndent() != depth) {
                println("leaving b/c currentDepth = ${currentIndent()} != $depth")
                break
            }
            scanner.consume(TokenType.NEWLINE)
        }
        return if (result.size == 1) result.first().value else SymbolExpression(null, "seq", result)
    }

    fun parseStatement(depth: Int): Expression {
        return if (scanner.tryConsume("let")) {
            parseVariableDeclaration(depth)
        } else {
            var result = parseExpression(depth)
            if (scanner.tryConsume("=")) {
                throw RuntimeException("TBD")
            }
            result
        }
    }

    fun parseType(depth: Int): Type {
        val name = (scanner.consume(TokenType.IDENTIFIER) { "Type name identifier expected." }).text
        return TypeReference(name)
    }


    fun parseVariableDeclaration(depth: Int): Expression {
        val name = scanner.consume(TokenType.IDENTIFIER).text
        scanner.consume("=")
        val value = parseExpression(depth)
        return VariableDeclaration(name, value)
    }


    companion object {

        fun parseProgram(code: String) =
            SugarcoatParser(Scanner(NewlineFilter(SugarcoatLexer(code)), TokenType.EOF)).parseProgram()
    }
}