package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.model.GlobalRuntimeContext
import org.kobjects.sugarcoat.parser.Position

data class LocalRuntimeContext(
    val globalRuntimeContext: GlobalRuntimeContext,
    val instance: Any?,
    val parent: LocalRuntimeContext? = null,
) {
    private val symbols = mutableMapOf<String, Any>()

    fun getOrNull(name: String): Any? = symbols[name] ?: parent?.getOrNull(name)

    fun get(name: String, position: Position = Position.UNKNOWN): Any = getOrNull(name) ?: throw IllegalStateException("$position: Local variable '$name' not found in $this")

    fun set(name: String, value: Any, position: Position = Position.UNKNOWN) {
        if (symbols.containsKey(name)) {
            symbols[name] = value
        } else {
            parent?.set(name, value) ?: throw IllegalStateException("$position: Local variable '$name' not found in $this")
        }
    }

    fun declare(name: String, value: Any, position: Position = Position.UNKNOWN) {
        if (symbols.containsKey(name)) {
            throw IllegalStateException("$position: Local variable '$name' already exists in $this")
        }
        symbols[name] = value
    }



    override fun toString() = "LRT(instance: $instance; symbols: $symbols)"
}