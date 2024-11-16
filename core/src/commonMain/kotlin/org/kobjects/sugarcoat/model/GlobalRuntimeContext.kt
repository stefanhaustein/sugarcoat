package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.type.Type

class GlobalRuntimeContext(
    val program: Program,
    val printFn: (String) -> Unit = ::print
) {
    val symbols = mutableMapOf<StaticFieldDefinition, Any>()
}
