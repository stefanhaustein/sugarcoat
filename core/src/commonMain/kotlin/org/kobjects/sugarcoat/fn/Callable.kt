package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Scope
import org.kobjects.sugarcoat.model.Instance

interface Callable {
    fun call(
        receiver: Instance?,
        children: List<ParameterReference>,
        parameterScope: Scope
    ): Scope
}