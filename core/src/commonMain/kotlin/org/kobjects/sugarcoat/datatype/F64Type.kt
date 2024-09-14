package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.ast.AbstractClassifierDefinition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.ResolvedType
import org.kobjects.sugarcoat.datatype.I64Type.Instance
import org.kobjects.sugarcoat.datatype.I64Type.addNativeMethod
import org.kobjects.sugarcoat.runtime.RuntimeContext
import kotlin.math.pow

object F64Type : NativeType("F64") {

    init {
        addNativeMethod(F64Type, "+", "other" to F64Type) { Instance(it[0].f64() + it[1].f64() ) }
        addNativeMethod(F64Type, "-", "other" to F64Type) { Instance(it[0].f64() - it[1].f64() ) }
        addNativeMethod(F64Type, "*", "other" to F64Type) { Instance(it[0].f64() * it[1].f64() ) }
        addNativeMethod(F64Type, "**", "other" to F64Type) { Instance(it[0].f64().pow(it[1].f64()) ) }
        addNativeMethod(F64Type, "/", "other" to F64Type) { Instance(it[0].f64() / it[1].f64() ) }
        addNativeMethod(F64Type, "%", "other" to F64Type) { Instance(it[0].f64() % it[1].f64() ) }
        addNativeMethod(BoolType, "==", "other" to F64Type) { BoolType.Instance(it[0].f64() == it[1].f64() )}
        addNativeMethod(BoolType, "!=", "other" to F64Type) { BoolType.Instance(it[0].f64() != it[1].f64() )}
    }

    data class Instance(val value: Double) : NativeInstance() {
        override val type: NativeType
            get() = F64Type

        override fun toString() = value.toString()

        override fun f64(): Double = value
    }

}