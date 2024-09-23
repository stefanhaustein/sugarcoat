package org.kobjects.sugarcoat.datatype

object I64Type : NativeType("I64") {

    init {
        addNativeMethod(I64Type, "%", "other" to I64Type) { it.i64(0) % it.i64(1) }
        addNativeMethod(I64Type, "+", "other" to I64Type) { it.i64(0) + it.i64(1) }
        addNativeMethod(I64Type, "-", "other" to I64Type) { it.i64(0) - it.i64(1) }
        addNativeMethod(I64Type, "//", "other" to I64Type) { it.i64(0) / it.i64(1) }
        addNativeMethod(BoolType, "==", "other" to I64Type) { it.i64(0) == it.i64(1) }
        addNativeMethod(BoolType, "!=", "other" to I64Type) { it.i64(0) == it.i64(1) }
    }

}