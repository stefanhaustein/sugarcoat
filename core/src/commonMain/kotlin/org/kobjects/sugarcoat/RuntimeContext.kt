package org.kobjects.sugarcoat


interface RuntimeContext {
    fun evalSymbol(name: String, children: List<Parameter>, parameterContext: RuntimeContext): RuntimeContext


    companion object {

        fun of(value: Any) = when(value) {
            is Double -> DoubleContext(value)
            is Boolean -> BooleanContext(value)
            is Long -> LongContext(value)
            is String -> StringContext(value)
            else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
        }
    }
}