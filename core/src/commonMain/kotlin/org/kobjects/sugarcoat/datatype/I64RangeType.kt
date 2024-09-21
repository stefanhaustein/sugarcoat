package org.kobjects.sugarcoat.datatype

object I64RangeType : NativeType("I64Range") {

    data class Instance(
        val value: LongRange
    ) : NativeInstance() {

        override val type: NativeType
            get() = I64RangeType
    }
}