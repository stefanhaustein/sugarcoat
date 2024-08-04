package org.kobjects.sugarcoat

import org.kobjects.parsek.tokenizer.Scanner

class SugarcoatParser private constructor(val scanner: Scanner<TokenType>) {
    val functions = mutableMapOf<String, Lambda>()

    private fun parseExpression(): Evaluable = ExpressionParser.parseExpression(scanner)

    private fun currentIndent(): Int {
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
                    "def" -> parserDef()
                    else -> throw scanner.exception("Unexpected token.")
                }
            }
        }
        require(functions.containsKey("main")) {
            "main() function not found."
        }
        return Program(functions.toMap())
    }

    fun parserDef() {
        scanner.consume("def")
        val name = scanner.consume(TokenType.IDENTIFIER) { "Identifier expected after 'def'." }.text
        scanner.consume("(") { "Opening brace expected after function name '$name'." }
        val parameters = mutableListOf<DeclaredParameter>()
        if (!scanner.tryConsume(")")) {
            do {
                parameters.add(DeclaredParameter(scanner.consume(TokenType.IDENTIFIER) { "Parameter name expected." }.text))
            } while (scanner.tryConsume(","))
            scanner.consume(")") { "Closing brace or comma (')' or ',') expected after parameter" }
        }
        scanner.consume(":") { "Colon expected after function parameter list." }
        val body = parseBody(0)
        val fn = Lambda(parameters, body)
        functions[name] = fn
    }

    fun parseBody(parentDepth: Int): Evaluable {
        val depth = currentIndent()
        if (depth <= parentDepth) {
            return Symbol("seq", false)
        }
        scanner.consume(TokenType.NEWLINE)
        val result = mutableListOf<Parameter>()
        while(true) {
            if (scanner.current.type != TokenType.NEWLINE) {
                val statement = parseStatement(depth)
                println("parsed: $statement")
                result.add(Parameter("", statement))
            }
            if (currentIndent() != depth) {
                break
            }
            scanner.consume(TokenType.NEWLINE)
        }
        return if (result.size == 1) result.first().value else Symbol(null, "seq", result)
    }

    fun parseStatement(depth: Int): Evaluable {
        var result = parseExpression()
        if (scanner.tryConsume("=")) {
            throw RuntimeException("TBD")
        }

        if (scanner.tryConsume(",")) {
            require (result is Symbol) { "Call expected for ','" }
            val symbol = result as Symbol
            require (symbol.children.size == 1) { "Missing argument before ','" }
            result = parseExtraArguments(depth, symbol)
        } else if (scanner.current.type != TokenType.NEWLINE
            && scanner.current.text != ":"
            && result is Symbol && result.children.isEmpty()) {
            result = parseExtraArguments(depth, result as Symbol)
        }

        if (scanner.tryConsume(":")) {
            require (result is Symbol) { "Symbol expected for ':'" }
            val symbol = result as Symbol
            val arguments = symbol.children.toMutableList()
            arguments.add(Parameter("", parseLambdaArgumentsAndBody(depth)))
            result = Symbol(symbol.receiver, symbol.name, arguments.toList())

            while (currentIndent() == depth && scanner.lookAhead(1).type == TokenType.PROPERTY) {
                val innerArguments = ParameterListBuilder()
                scanner.consume(TokenType.NEWLINE)
                val property = scanner.consume(TokenType.PROPERTY).text.substring(1)
                if (scanner.current.text != ":") {
                    innerArguments.add(parseExpression())
                }
                scanner.consume(":") { "Colon expected" }
                val body = parseLambdaArgumentsAndBody(depth)
                innerArguments.add(body)
                result = Symbol(result, property, innerArguments.build())
            }
        }

        return result
    }

    fun parseLambdaArgumentsAndBody(depth: Int): Evaluable {

        val parmeters = mutableListOf<DeclaredParameter>()
        if (scanner.current.type == TokenType.IDENTIFIER) {
            do {
                parmeters.add(DeclaredParameter(scanner.consume(TokenType.IDENTIFIER).text))
            } while (scanner.tryConsume(","))
        }


        val parsedBody = parseBody(depth)
        return if (parmeters.isEmpty()) parsedBody else Lambda(parmeters.toList(), parsedBody)

    }

    fun parseExtraArguments(depth: Int, symbol: Symbol): Symbol {
        val arguments = symbol.children.toMutableList()
        do {
            arguments.add(Parameter("", parseExpression()))
        } while (scanner.tryConsume(","))
        return Symbol(symbol.receiver, symbol.name, arguments.toList())
    }



    companion object {

        fun parseProgram(code: String) =
            SugarcoatParser(Scanner(NewlineFilter(SugarcoatLexer(code)), TokenType.EOF)).parseProgram()
    }
}