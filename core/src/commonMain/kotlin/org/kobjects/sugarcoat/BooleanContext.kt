package org.kobjects.sugarcoat

class BooleanContext(val value: Boolean) : RuntimeContext {

    override fun evalSymbol(
        name: String,
        children: List<Parameter>,
        parameterContext: RuntimeContext
    ): Any {
        when (name) {
            else -> throw UnsupportedOperationException("Method $name unsupported for boolean values.")
        }
    }
}