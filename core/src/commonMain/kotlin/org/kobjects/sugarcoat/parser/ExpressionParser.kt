package org.kobjects.sugarcoat.parser

import org.kobjects.parsek.expressionparser.ConfigurableExpressionParser
import org.kobjects.parsek.tokenizer.Scanner
import org.kobjects.sugarcoat.runtime.ProgramContext
import org.kobjects.sugarcoat.ast.FunctionDefinition
import org.kobjects.sugarcoat.ast.LambdaExpression
import org.kobjects.sugarcoat.ast.ParameterDefinition
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ImplicitType
import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.ast.ParameterListBuilder
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.Program
import org.kobjects.sugarcoat.ast.SymbolExpression
import org.kobjects.sugarcoat.datatype.VoidType


object ExpressionParser : ConfigurableExpressionParser<Scanner<TokenType>, ParsingContext, Expression>(
    { scanner, context -> ExpressionParser.parsePrimary(scanner, context) },
    prefix(9, "+", "-") { _, _, name, operand -> SymbolExpression(operand, name, 9) },
    infix(8, "**") { _, _, _, left, right -> SymbolExpression(left, "**", 8, right) },
    infix(7, "*", "/", "%", "//") { _, _, name, left, right -> SymbolExpression(left, name, 7, right) },
    infix(6, "+", "-") { _, _, name, left, right -> SymbolExpression(left, name, 6, right) },
    infix(5, "<", "<=", ">", ">=") { _, _, name, left, right -> SymbolExpression(left, name, 5, right) },
    infix(4, "==", "!=") { _, _, name, left, right -> SymbolExpression(left, name, 4, right) },
    infix(3, "&&") { _, _, _, left, right -> SymbolExpression(left, "&&", 3, right) },
    infix(2, "||") { _, _, _, left, right -> SymbolExpression(left, "||", 2, right) },
    prefix(1, "!") { _, _, _, operand -> SymbolExpression(operand, "!", 1) }
) {
    private fun parsePrimary(tokenizer: Scanner<TokenType>, context: ParsingContext): Expression =
        when (tokenizer.current.type) {
            TokenType.NUMBER -> {
                val text = tokenizer.consume().text
                LiteralExpression(if (text.contains(".") || text.contains("e") || text.contains("E")) text.toDouble() else text.toLong())
            }
            TokenType.STRING -> {
                val text = tokenizer.consume().text
                LiteralExpression(
                    text.substring(1, text.length - 1)
                        .replace("\\n", "\n")
                )
            }
            TokenType.IDENTIFIER -> {
                var name = tokenizer.consume().text
                val children = parseParameterList(tokenizer, context)
                SymbolExpression(null, name, children)
            }
            TokenType.SYMBOL -> {
                if (!tokenizer.tryConsume("(")) {
                    throw tokenizer.exception("Unrecognized primary expression.")
                }
                val expr = parseExpression(tokenizer, context)
                tokenizer.consume(")")
                expr
            }
            else ->
                throw tokenizer.exception("Unrecognized primary expression.")
    }

    fun parseParameterList(scanner: Scanner<TokenType>, context: ParsingContext): List<ParameterReference> {
        val builder = ParameterListBuilder()
        if (scanner.tryConsume("(")) {
            if (scanner.current.text != ")") {
                do {
                    val parameterName =
                        if (scanner.current.type == TokenType.IDENTIFIER && scanner.lookAhead(1).text == "=") {
                            val name = scanner.consume(TokenType.IDENTIFIER).text
                            scanner.consume("=")
                            name
                        } else ""
                    val value = parseExpression(scanner, context)
                    builder.add(parameterName, value)
                } while (scanner.tryConsume(","))
            }
            scanner.consume(")")
        }

        if (scanner.tryConsume(":")) {
            builder.add(parseLambdaArgumentsAndBody(scanner, context))
        }

        while (scanner.current.type == TokenType.NEWLINE && context.parser.currentIndent() == context.depth && scanner.lookAhead(1).text == "--") {
            scanner.consume(TokenType.NEWLINE)
            scanner.consume("--")
            val property = scanner.consume(TokenType.IDENTIFIER).text
            val expr = if (scanner.current.text == "(") context.parser.parseExpression(context.depth) else null
            scanner.consume(":") { "Colon expected" }
            val body = parseLambdaArgumentsAndBody(scanner, context)
            if (expr == null) {
                builder.add(property, body)
            } else {
                builder.add(property, SymbolExpression("pair", expr, body))
            }
        }

        return builder.build()
    }


    fun parseLambdaArgumentsAndBody(scanner: Scanner<TokenType>, context: ParsingContext): Expression {

        val parmeters = mutableListOf<ParameterDefinition>()
        if (scanner.current.type == TokenType.IDENTIFIER) {
            do {
                parmeters.add(ParameterDefinition(scanner.consume(TokenType.IDENTIFIER).text, ImplicitType()))
            } while (scanner.tryConsume(","))
        }

        val parsedBody = context.parser.parseBody(context.depth)
        return if (parmeters.isEmpty()) parsedBody else LambdaExpression(FunctionDefinition(VoidType, parmeters.toList(), ImplicitType(), parsedBody))
    }


    fun eval(expression: String): Any {
        val scanner = Scanner(SugarcoatLexer(expression), TokenType.EOF)
        val parsed = parseExpression(
            scanner, ParsingContext(0, SugarcoatParser(scanner))
        )
        return parsed.eval(ProgramContext(Program()))
    }
}