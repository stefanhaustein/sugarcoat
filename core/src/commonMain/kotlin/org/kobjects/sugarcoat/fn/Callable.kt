package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.base.RuntimeContext

interface Callable {
    fun call(receiver: RuntimeContext, children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext
}