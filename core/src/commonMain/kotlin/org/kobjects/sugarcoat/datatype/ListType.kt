package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterReference
import org.kobjects.sugarcoat.base.RuntimeContext

class ListType {


    class Instance(
        val value: List<Any>
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