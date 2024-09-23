package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.model.Instance

interface Callable {
    fun call(
        receiver: Any?,
        children: List<ParameterReference>,
        parameterScope: RuntimeContext
    ): Any
}