package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterReference
import org.kobjects.sugarcoat.base.RuntimeContext

object I64RangeType : NativeType("I64Range") {

    data class Instance(
        val value: LongRange
    ) : NativeInstance() {

        override val type: NativeType
            get() = I64RangeType
    }
}