package org.kobjects.sugarcoat.datatype

object I64Type : NativeType("I64") {

    init {
        addNativeMethod(I64Type, "%", "other" to I64Type) { Instance(it[0].i64() % it[1].i64() )}
        addNativeMethod(I64Type, "+", "other" to I64Type) { Instance(it[0].i64() + it[1].i64() )}
        addNativeMethod(I64Type, "-", "other" to I64Type) { Instance(it[0].i64() - it[1].i64() )}
        addNativeMethod(I64Type, "//", "other" to I64Type) { Instance(it[0].i64() / it[1].i64() )}
        addNativeMethod(BoolType, "==", "other" to I64Type) { BoolType.Instance(it[0].i64() == it[1].i64() )}
        addNativeMethod(BoolType, "!=", "other" to I64Type) { BoolType.Instance(it[0].i64() == it[1].i64() )}

    }

    data class Instance(val value: Long) : NativeInstance() {
        override val type: NativeType
            get() = I64Type

        override fun i64() = value

        override fun toString() = value.toString()
    }
}