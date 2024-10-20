package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ParameterReference

interface Callable {
    val static: Boolean
    fun call(
        receiver: Any?,
        children: List<ParameterReference>,
        parameterScope: LocalRuntimeContext
    ): Any
}