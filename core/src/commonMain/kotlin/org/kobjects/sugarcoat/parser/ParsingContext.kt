package org.kobjects.sugarcoat.parser

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.model.Program

data class ParsingContext(
    val program: Program,
    val namespace: Namespace = program,
    val depth: Int = 0

    ) {

}
