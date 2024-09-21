package org.kobjects.sugarcoat.parser

import org.kobjects.sugarcoat.base.Definition
import org.kobjects.sugarcoat.model.Program

data class ParsingContext(
    val program: Program,
    val definition: Definition = program,
    val depth: Int = 0

    ) {

}
