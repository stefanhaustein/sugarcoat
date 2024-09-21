package org.kobjects.sugarcoat.datatype

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