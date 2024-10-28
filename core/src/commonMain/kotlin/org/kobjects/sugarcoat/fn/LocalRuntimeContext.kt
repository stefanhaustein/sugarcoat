package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.model.GlobalRuntimeContext

class LocalRuntimeContext(
    val globalRuntimeContext: GlobalRuntimeContext,
    val instance: Any?
) {
    val symbols = mutableMapOf<String, Any>()

}