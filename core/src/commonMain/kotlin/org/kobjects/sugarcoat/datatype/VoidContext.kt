package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.runtime.RuntimeContext
import org.kobjects.sugarcoat.ast.ParameterReference

object VoidContext : RuntimeContext {
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