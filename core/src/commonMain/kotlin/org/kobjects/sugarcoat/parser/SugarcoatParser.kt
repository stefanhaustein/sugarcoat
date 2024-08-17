package org.kobjects.sugarcoat.parser

import org.kobjects.parsek.tokenizer.Scanner
import org.kobjects.sugarcoat.ast.LambdaDeclaration
import org.kobjects.sugarcoat.ast.ParameterDeclaration
import org.kobjects.sugarcoat.ast.Node
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.Program
import org.kobjects.sugarcoat.ast.SymbolNode

class SugarcoatParser internal constructor(val scanner: Scanner<TokenType>) {
    val functions = mutableMapOf<String, LambdaDeclaration>()

    internal fun parseExpression(depth: Int): Node = ExpressionParser.parseExpression(scanner, ParsingContext(depth, this))

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
                    "fn" -> parseFn()
                    else -> throw scanner.exception("Unexpected token.")
                }
            }
        }
        require(functions.containsKey("main")) {
            "main() function not found."
        }
        return Program(functions.toMap())
    }

    fun parseFn() {
        scanner.consume("fn")
        val name = scanner.consume(TokenType.IDENTIFIER) { "Identifier expected after 'def'." }.text
        scanner.consume("(") { "Opening brace expected after function name '$name'." }
        val parameters = mutableListOf<ParameterDeclaration>()
        if (!scanner.tryConsume(")")) {
            do {
                parameters.add(ParameterDeclaration(scanner.consume(TokenType.IDENTIFIER) { "Parameter name expected." }.text))
            } while (scanner.tryConsume(","))
            scanner.consume(")") { "Closing brace or comma (')' or ',') expected after parameter" }
        }
        scanner.consume(":") { "Colon expected after function parameter list." }
        val body = parseBody(0)
        val fn = LambdaDeclaration(parameters, body)
        functions[name] = fn
    }

    fun parseBody(parentDepth: Int): Node {
        val depth = currentIndent()
        println("ParseBody; parentDepth: $parentDepth; depth: $depth")
        if (depth <= parentDepth) {
            return SymbolNode("seq")
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
        return if (result.size == 1) result.first().value else SymbolNode(null, "seq", result)
    }

    fun parseStatement(depth: Int): Node {
        var result = parseExpression(depth)
        if (scanner.tryConsume("=")) {
            throw RuntimeException("TBD")
        }

        return result
    }



    companion object {

        fun parseProgram(code: String) =
            SugarcoatParser(Scanner(NewlineFilter(SugarcoatLexer(code)), TokenType.EOF)).parseProgram()
    }
}