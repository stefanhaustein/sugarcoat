package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.RuntimeContext
import org.kobjects.sugarcoat.model.Instance

interface Callable {
    fun call(
        receiver: Instance?,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext
}