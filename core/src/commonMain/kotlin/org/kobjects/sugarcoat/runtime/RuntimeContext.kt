package org.kobjects.sugarcoat.runtime

import org.kobjects.sugarcoat.datatype.BoolType
import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.datatype.StringType
import org.kobjects.sugarcoat.ast.ParameterReference


interface RuntimeContext {
    fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext

    fun i32(): Int = throw UnsupportedOperationException()
    fun i64(): Long = throw UnsupportedOperationException()
    fun f32(): Float = throw UnsupportedOperationException()
    fun f64(): Double = throw UnsupportedOperationException()


    companion object {

        fun of(value: Any) = when(value) {
            is Double -> F64Type.Instance(value)
            is Boolean -> BoolType.Instance(value)
            is Long -> I64Type.Instance(value)
            is String -> StringType.Instance(value)
            else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
        }
    }
}