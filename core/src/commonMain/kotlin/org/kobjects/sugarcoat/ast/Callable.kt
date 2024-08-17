package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RuntimeContext

interface Callable {
    fun eval(children: List<ParameterReference>, callerContext: RuntimeContext): RuntimeContext
}