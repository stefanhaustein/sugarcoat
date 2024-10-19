package org.kobjects.sugarcoat.parser

import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.Program

data class ParsingContext(
    val program: Program,
    val namespace: Classifier = program,
    val depth: Int = 0

    ) {

}
