package org.kobjects.sugarcoat.parser

import org.kobjects.sugarcoat.ast.Program

data class ParsingContext(
    val program: Program,
    val depth: Int
) {

}
