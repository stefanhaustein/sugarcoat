package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.runtime.RuntimeContext

class RangeContext(
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