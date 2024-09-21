package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterReference
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.RuntimeContext

object VoidType : ResolvedType {



    object Instance : RuntimeContext {
        override fun evalSymbol(
            name: String,
            children: List<ParameterReference>,
            parameterContext: RuntimeContext
        ): RuntimeContext {
            when (name) {
                else -> throw UnsupportedOperationException("Method $name unsupported for Void.")
            }
        }
    }
}