package org.kobjects.sugarcoat

import org.kobjects.parsek.expressionparser.ConfigurableExpressionParser
import org.kobjects.parsek.tokenizer.Scanner
import org.kobjects.sugarcoat.function.LambdaDeclaration
import org.kobjects.sugarcoat.function.ParameterDeclaration


object ExpressionParser : ConfigurableExpressionParser<Scanner<TokenType>, ParsingContext, Evaluable>(
    { scanner, context -> ExpressionParser.parsePrimary(scanner, context) },
    prefix(9, "+", "-") { _, _, name, operand -> SymbolReference(operand, name, 9) },
    infix(8, "**") { _, _, _, left, right -> SymbolReference(left, "**", 8, right) },
    infix(7, "*", "/", "%", "//") { _, _, name, left, right -> SymbolReference(left, name, 7, right) },
    infix(6, "+", "-") { _, _, name, left, right -> SymbolReference(left, name, 6, right) },
    infix(5, "<", "<=", ">", ">=") { _, _, name, left, right -> SymbolReference(left, name, 5, right) },
    infix(4, "==", "!=") { _, _, name, left, right -> SymbolReference(left, name, 4, right) },
    infix(3, "&&") { _, _, _, left, right -> SymbolReference(left, "&&", 3, right) },
    infix(2, "||") { _, _, _, left, right -> SymbolReference(left, "||", 2, right) },
    prefix(1, "!") { _, _, _, operand -> SymbolReference(operand, "!", 1) }
) {
    private fun parsePrimary(tokenizer: Scanner<TokenType>, context: ParsingContext): Evaluable =
        when (tokenizer.current.type) {
            TokenType.NUMBER -> {
                val text = tokenizer.consume().text
                Literal(if (text.contains(".") || text.contains("e") || text.contains("E")) text.toDouble() else text.toLong())
            }
            TokenType.STRING -> {
                val text = tokenizer.consume().text
                Literal(
                    text.substring(1, text.length - 1)
                        .replace("\\n", "\n")
                )
            }
            TokenType.IDENTIFIER -> {
                var name = tokenizer.consume().text
                val children = parseParameterList(tokenizer, context)
                SymbolReference(null, name, children)
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
                builder.add(property, SymbolReference("pair", expr, body))
            }
        }

        return builder.build()
    }



    fun parseLambdaArgumentsAndBody(scanner: Scanner<TokenType>, context: ParsingContext): Evaluable {

        val parmeters = mutableListOf<ParameterDeclaration>()
        if (scanner.current.type == TokenType.IDENTIFIER) {
            do {
                parmeters.add(ParameterDeclaration(scanner.consume(TokenType.IDENTIFIER).text))
            } while (scanner.tryConsume(","))
        }

        val parsedBody = context.parser.parseBody(context.depth)
        return if (parmeters.isEmpty()) parsedBody else LambdaDeclaration(parmeters.toList(), parsedBody)
    }


    fun eval(expression: String): Any {
        val scanner = Scanner(SugarcoatLexer(expression), TokenType.EOF)
        val parsed = parseExpression(
            scanner, ParsingContext(0, SugarcoatParser(scanner))
        )
        return parsed.eval(ProgramContext(Program(emptyMap())))
    }
}