package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.model.GlobalRuntimeContext

data class LocalRuntimeContext(
    val globalRuntimeContext: GlobalRuntimeContext,
    val instance: Any?
) {
    val symbols = mutableMapOf<String, Any>()

    override fun toString() = "LRT(instance: $instance; symbols: $symbols)"
}