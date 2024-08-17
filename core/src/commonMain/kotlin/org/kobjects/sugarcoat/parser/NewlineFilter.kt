package org.kobjects.sugarcoat.parser

import org.kobjects.parsek.tokenizer.Filter
import org.kobjects.parsek.tokenizer.Token
import kotlin.math.max

class NewlineFilter(lexer: SugarcoatLexer) : Filter<TokenType>(lexer) {
    var bracketDepth = 0

    override fun accept(token: Token<TokenType>): Boolean {
        when (token.text) {
            "{",
            "[",
            "(" -> bracketDepth++
            "}",
            "]",
            ")" -> bracketDepth = max(0, bracketDepth - 1)
        }
        return bracketDepth == 0 || token.type != TokenType.NEWLINE
    }
}