package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.base.RootContext

object I64Type : NativeType("I64", RootContext) {

    init {
        addNativeMethod(I64Type, "%", "other" to I64Type) { it.i64(0) % it.i64(1) }
        addNativeMethod(I64Type, "+", "other" to I64Type) { it.i64(0) + it.i64(1) }
        addNativeMethod(I64Type, "-", "other" to I64Type) { it.i64(0) - it.i64(1) }
        addNativeMethod(I64Type, "//", "other" to I64Type) { it.i64(0) / it.i64(1) }
        addNativeMethod(BoolType, "==", "other" to I64Type) { it.i64(0) == it.i64(1) }
        addNativeMethod(BoolType, "!=", "other" to I64Type) { it.i64(0) == it.i64(1) }
    }

}