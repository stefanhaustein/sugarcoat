package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.datatype.BooleanContext
import org.kobjects.sugarcoat.datatype.DoubleContext
import org.kobjects.sugarcoat.datatype.LongContext
import org.kobjects.sugarcoat.datatype.StringContext


interface RuntimeContext {
    fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext


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