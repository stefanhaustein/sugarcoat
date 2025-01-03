package org.kobjects.sugarcoat.parser

import org.kobjects.parsek.expressionparser.ConfigurableExpressionParser
import org.kobjects.parsek.tokenizer.Scanner
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.ast.LiteralExpression.Companion.unescape
import org.kobjects.sugarcoat.ast.ParameterListBuilder
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.ParenthesizedExpression
import org.kobjects.sugarcoat.ast.ResolutionContext
import org.kobjects.sugarcoat.ast.UnresolvedAsExpression
import org.kobjects.sugarcoat.ast.UnresolvedLambdaExpression
import org.kobjects.sugarcoat.model.Program
import org.kobjects.sugarcoat.ast.UnresolvedSymbolExpression
import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.model.GlobalRuntimeContext
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.SugarcoatParser.parseType
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.Type


fun Scanner<TokenType>.position() = Position(current.line, current.col)

object ExpressionParser : ConfigurableExpressionParser<Scanner<TokenType>, ParsingContext, Expression>(
    { scanner, context -> ExpressionParser.parsePrimary(scanner, context) },
    prefix(10, "+", "-") { scanner, context, name, operand ->
        if (operand is LiteralExpression && operand.value is Number) {
            if (name == "+") {
                operand
            } else {
                LiteralExpression(
                    operand.position,
                    when (operand.value) {
                        is Int -> -operand.value
                        is Long -> -operand.value
                        is Double -> -operand.value
                        is Float -> -operand.value
                        else -> UnresolvedSymbolExpression(scanner.position(), operand, "0$name")
                    })
            }
        } else {
            UnresolvedSymbolExpression(scanner.position(), operand, "0$name")
        }
    },
    infix(9, "**") { scanner, context, _, left, right -> UnresolvedSymbolExpression(scanner.position(), left, "**", right) },
    infix(8, "as") { scanner, context, _, left, right -> UnresolvedAsExpression(scanner.position(), left, right) },
    infix(7, "*", "/", "%") { scanner, context, name, left, right -> UnresolvedSymbolExpression(scanner.position(), left, name, right) },
    infix(6, "+", "-") { scanner, context, name, left, right -> UnresolvedSymbolExpression(scanner.position(), left, name, right) },
    infix(5, "<", "<=", ">", ">=") { scanner, context, name, left, right -> UnresolvedSymbolExpression(scanner.position(), left, name, right) },
    infix(4, "==", "!=") { scanner, context, name, left, right -> UnresolvedSymbolExpression(scanner.position(), left, name, right) },
    infix(3, "&&") { scanner, context, _, left, right -> UnresolvedSymbolExpression(scanner.position(), left, "&&", right) },
    infix(2, "||") { scanner, context, _, left, right -> UnresolvedSymbolExpression(scanner.position(), left, "||", right) },
    prefix(1, "!") { scanner, context, _, operand -> UnresolvedSymbolExpression(scanner.position(), operand, "!") }
) {
    private fun parsePrimary(tokenizer: Scanner<TokenType>, context: ParsingContext): Expression {
        var expr = when (tokenizer.current.type) {
            TokenType.NUMBER -> {
                val text = tokenizer.consume().text
                LiteralExpression(tokenizer.position(), if (text.contains(".") || text.contains("e") || text.contains("E")) text.toDouble() else text.toLong())
            }

            TokenType.STRING -> {
                val text = tokenizer.consume().text
                LiteralExpression(
                    tokenizer.position(),
                    text.substring(1, text.length - 1).unescape()
                )
            }

            TokenType.IDENTIFIER -> {
                var name = tokenizer.consume().text
                when (name) {
                    "true" -> LiteralExpression(tokenizer.position(), true)
                    "false" -> LiteralExpression(tokenizer.position(), false)
                    else -> {
                        val parens = tokenizer.current.text == "("
                        val children = parseParameterList(tokenizer, context)
                        UnresolvedSymbolExpression(
                            tokenizer.position(),
                            null,
                            name,
                            parens,
                            children
                        )
                    }
                }
            }

            TokenType.SYMBOL -> {
                if (tokenizer.tryConsume("(")) {
                    val expr = parseExpression(tokenizer, context)
                    tokenizer.consume(")")
                    ParenthesizedExpression(tokenizer.position(), expr)
                } else if (tokenizer.tryConsume("[")) {
                    val builder = ParameterListBuilder()
                    if (tokenizer.current.text != "]") {
                        do {
                            builder.add(parseExpression(tokenizer, context))
                        } while (tokenizer.tryConsume(","))
                    }
                    tokenizer.consume("]") { "',' or ']' expected" }
                    UnresolvedSymbolExpression(
                        tokenizer.position(),
                        LiteralExpression(tokenizer.position(), ListType(GenericType("E"))),
                        "create",
                        true,
                        builder.build()
                    )
                } else {
                    throw tokenizer.exception("'(' or '[' expected.")
                }
            }

            else ->
                throw tokenizer.exception("Unrecognized primary expression.")
        }
        while (tokenizer.current.type == TokenType.PROPERTY || tokenizer.current.text == "[" || tokenizer.current.text == "(") {
            if (tokenizer.tryConsume("(")) {
                val builder = ParameterListBuilder()
                if (tokenizer.current.text != ")") {
                    do {
                        builder.add(parseExpression(tokenizer, context))
                    } while (tokenizer.tryConsume(","))
                }
                tokenizer.consume(")") { "',' or ']' expected" }
                expr = UnresolvedSymbolExpression(
                    tokenizer.position(),
                    expr,
                    "()",
                    true,
                    builder.build()
                )
            }
            else if (tokenizer.tryConsume("[")) {
                val builder = ParameterListBuilder()
                if (tokenizer.current.text != "]") {
                    do {
                        builder.add(parseExpression(tokenizer, context))
                    } while (tokenizer.tryConsume(","))
                }
                tokenizer.consume("]") { "',' or ']' expected" }
                expr = UnresolvedSymbolExpression(
                    tokenizer.position(),
                    expr,
                    "[]",
                    true,
                    builder.build()
                )
            } else {
                val name = tokenizer.consume().text.substring(1)
                val hasParens = tokenizer.current.text == "("
                val parameterList = parseParameterList(tokenizer, context)
                expr = UnresolvedSymbolExpression(
                    tokenizer.position(),
                    expr,
                    name,
                    hasParens,
                    parameterList
                )
            }
        }
        return expr
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

        val firstOptionalLambda = parseOptionalLambda(scanner, context)
        if (firstOptionalLambda != null) {
            builder.add(firstOptionalLambda)
        }


        while (scanner.current.type == TokenType.NEWLINE && SugarcoatParser.currentIndent(scanner) == context.depth && scanner.lookAhead(1).text == "--") {
            scanner.consume(TokenType.NEWLINE)
            scanner.consume("--")
            val property = scanner.consume(TokenType.IDENTIFIER).text
            val expr = if (scanner.current.text == "(") SugarcoatParser.parseExpression(scanner, context) else null
            val optionalLambda = parseOptionalLambda(scanner, context)
            if (expr != null) {
                if (optionalLambda != null) {
                    builder.add(property, UnresolvedSymbolExpression(scanner.position(), "Pair", expr, optionalLambda))
                } else {
                    builder.add(property, expr)
                }
            } else if (optionalLambda != null) {
                builder.add(property, optionalLambda)
            } else {
                throw scanner.exception("Parameter expression expected")
            }
        }

        return builder.build()
    }

    fun parseOptionalLambda(scanner: Scanner<TokenType>, context: ParsingContext): Expression? {
        if (scanner.tryConsume("::") || scanner.tryConsume(":")) {
            return parseLambdaArgumentsAndBody(scanner, context)
        }

        if (scanner.current.type == TokenType.NEWLINE && SugarcoatParser.currentIndent(scanner) > context.depth) {
            return SugarcoatParser.parseBlock(scanner, context)
        }

        return null
    }


    fun parseLambdaArgumentsAndBody(scanner: Scanner<TokenType>, context: ParsingContext): Expression {
        val position = scanner.position()
        val parameters = mutableListOf<Pair<String, Type?>>()
        if (scanner.current.type == TokenType.IDENTIFIER) {
            do {
                val parameterName = scanner.consume(TokenType.IDENTIFIER).text
                val type: Type? = if (scanner.tryConsume(":")) parseType(scanner, context) else null
                parameters.add(parameterName to type)
            } while (scanner.tryConsume(","))
        }

        /*
        val parentFn = context.namespace as FunctionDefinition

        val lambda = FunctionDefinition(
            parentFn,
            context.namespace,
            parentFn.static,
            "",
            parameters.map { ParameterDefinition(it.first, UnresolvedType("Lambda parameter")) },
            UnresolvedType("Lambda return type")
        )

        // parentFn.addChild(lambda)
*/
        val lambdaBody = SugarcoatParser.parseBlock(scanner, context)

        return UnresolvedLambdaExpression(position, parameters.toList(), lambdaBody)
    }


    fun eval(expression: String): Any {
        val scanner = Scanner(SugarcoatLexer(expression), TokenType.EOF)
        val parsed = parseExpression(
            scanner, ParsingContext(Program())
        )
        val program = Program()
        return parsed.resolve(ResolutionContext(program), null)
            .eval(LocalRuntimeContext(GlobalRuntimeContext(program), /*program,*/ null))
    }
}