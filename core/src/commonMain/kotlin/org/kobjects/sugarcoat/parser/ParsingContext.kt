package org.kobjects.sugarcoat.parser

import org.kobjects.sugarcoat.base.Element
import org.kobjects.sugarcoat.model.Program

data class ParsingContext(
    val program: Program,
    val namespace: Element = program,
    val depth: Int = 0

    ) {

}
