package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterReference
import org.kobjects.sugarcoat.base.RuntimeContext

class I64RangeType {


    class Instance(
        val value: LongRange
    ) : RuntimeContext {
        override fun evalSymbol(
            name: String,
            children: List<ParameterReference>,
            parameterContext: RuntimeContext
        ): RuntimeContext {
            TODO("Not yet implemented")
        }
    }
}