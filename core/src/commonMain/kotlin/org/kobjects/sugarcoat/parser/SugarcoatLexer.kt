package org.kobjects.sugarcoat.parser

import org.kobjects.parsek.tokenizer.RegularExpressions
import org.kobjects.parsek.tokenizer.Lexer
import kotlin.text.Regex

/** We include newlines here to simplify parsing for the "Mython" example. */
class SugarcoatLexer(input: String) : Lexer<TokenType>(
    input,
    RegularExpressions.HORIZONTAL_WHITESPACE to { null },
    Regex("""(\n[ \t]*)+""") to { TokenType.NEWLINE },
    Regex("""\.[\p{Alpha}_$][\p{Alpha}\d_$]*""".trimMargin()) to { TokenType.PROPERTY },
    RegularExpressions.NUMBER to { TokenType.NUMBER },
    RegularExpressions.DOUBLE_QUOTED_STRING to { TokenType.STRING },
    RegularExpressions.IDENTIFIER to { TokenType.IDENTIFIER },
    Regex("""\+\+|\+|--|->|-|\*\*|\*|%|<=|>=|==|=|<>|<|>|&&|&|\|\||\||\^|!=|!|\(|\)|,|\?|;|::|:|~|\[|]|\{|\}|//|/""") to { TokenType.SYMBOL })