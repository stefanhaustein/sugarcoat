package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Scope
import org.kobjects.sugarcoat.base.Type

interface Instance : Scope {
    val type: AbstractClassifierDefinition


    override fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: Scope
    ): Scope {
        val method = type.definitions[name]
        require(method is Callable) {
            "Unrecogized method $name: $method"
        }
        return method.call(this, children, parameterContext)
    }
}