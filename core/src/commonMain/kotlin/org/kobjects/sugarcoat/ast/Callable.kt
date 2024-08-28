package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RuntimeContext

interface Callable {
    fun call(receiver: RuntimeContext, children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext
}