package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.base.RootContext
import kotlin.math.pow

object F64Type : NativeType("F64", RootContext) {

    init {
        addNativeMethod(F64Type, "+", "other" to F64Type) { it.f64(0) + it.f64(1) }
        addNativeMethod(F64Type, "-", "other" to F64Type) { it.f64(0) - it.f64(1) }
        addNativeMethod(F64Type, "*", "other" to F64Type) { it.f64(0) * it.f64(1) }
        addNativeMethod(F64Type, "**", "other" to F64Type) { it.f64(0).pow(it.f64(1)) }
        addNativeMethod(F64Type, "/", "other" to F64Type) { it.f64(0) / it.f64(1) }
        addNativeMethod(F64Type, "%", "other" to F64Type) { it.f64(0) % it.f64(1) }
        addNativeMethod(BoolType, "==", "other" to F64Type) { it.f64(0) == it.f64(1) }
        addNativeMethod(BoolType, "!=", "other" to F64Type) { it.f64(0) != it.f64(1) }
    }

}