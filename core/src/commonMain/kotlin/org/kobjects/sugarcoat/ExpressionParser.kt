package org.kobjects.sugarcoat

import org.kobjects.parsek.expressionparser.ConfigurableExpressionParser
import org.kobjects.parsek.tokenizer.Scanner


object ExpressionParser : ConfigurableExpressionParser<Scanner<TokenType>, Unit, Evaluable>(
    { scanner, _ -> ExpressionParser.parsePrimary(scanner) },
    prefix(9, "+", "-") { _, _, name, operand -> Symbol(operand, name, 9) },
    infix(8, "**") { _, _, _, left, right -> Symbol(left, "**", 8, right) },
    infix(7, "*", "/", "%", "//") { _, _, name, left, right -> Symbol(left, name, 7, right) },
    infix(6, "+", "-") { _, _, name, left, right -> Symbol(left, name, 6, right) },
    infix(5, "<", "<=", ">", ">=") { _, _, name, left, right -> Symbol(left, name, 5, right) },
    infix(4, "==", "!=") { _, _, name, left, right -> Symbol(left, name, 4, right) },
    infix(3, "&&") { _, _, _, left, right -> Symbol(left, "&&", 3, right) },
    infix(2, "||") { _, _, _, left, right -> Symbol(left, "||", 2, right) },
    prefix(1, "!") { _, _, _, operand -> Symbol(operand, "!", 1) }
) {
    private fun parsePrimary(tokenizer: Scanner<TokenType>): Evaluable =
        when (tokenizer.current.type) {
            TokenType.NUMBER ->
                Literal(tokenizer.consume().text.toDouble())
            TokenType.STRING -> {
                val text = tokenizer.consume().text
                Literal(
                    text.substring(1, text.length - 1)
                        .replace("\\n", "\n")
                )
            }
            TokenType.IDENTIFIER -> {
                var name = tokenizer.consume().text
                val children = if (tokenizer.tryConsume("(")) parseParameterList(tokenizer, ")") else emptyList()
                Symbol(null, name, children)
            }
            TokenType.SYMBOL -> {
                if (!tokenizer.tryConsume("(")) {
                    throw tokenizer.exception("Unrecognized primary expression.")
                }
                val expr = parseExpression(tokenizer, Unit)
                tokenizer.consume(")")
                expr
            }
            else ->
                throw tokenizer.exception("Unrecognized primary expression.")
    }

    fun parseExpression(tokenizer: Scanner<TokenType>) = parseExpression(tokenizer, Unit)

    fun parseParameterList(tokenizer: Scanner<TokenType>, endToken: String): List<Parameter> {
        val builder = ParameterListBuilder()
        if (tokenizer.current.text != endToken) {
            do {
                val parameterName = if (tokenizer.current.type == TokenType.IDENTIFIER && tokenizer.lookAhead(1).text == "=") {
                    val name = tokenizer.consume(TokenType.IDENTIFIER).text
                    tokenizer.consume("=")
                    name
                } else ""
                val value = parseExpression(tokenizer, Unit)
                builder.add(parameterName, value)
            } while (tokenizer.tryConsume(","))
        }
        tokenizer.consume(endToken)
        return builder.build()
    }

    fun eval(expression: String) = parseExpression(
        Scanner(SugarcoatLexer(expression), TokenType.EOF)).eval(ProgramContext(Program(emptyMap())))
}