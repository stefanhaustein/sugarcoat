package org.kobjects.sugarcoat.parser

import org.kobjects.sugarcoat.ast.Definition
import org.kobjects.sugarcoat.ast.Program

data class ParsingContext(
    val program: Program,
    val definition: Definition = program,
    val depth: Int = 0

    ) {

}
