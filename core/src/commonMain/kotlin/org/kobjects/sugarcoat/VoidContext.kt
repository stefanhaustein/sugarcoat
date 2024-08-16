package org.kobjects.sugarcoat

object VoidContext : RuntimeContext {
    override fun evalSymbol(
        name: String,
        children: List<Parameter>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        when (name) {
            else -> throw UnsupportedOperationException("Method $name unsupported for Void.")
        }
    }
}