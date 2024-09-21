package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.RuntimeContext

interface Instance : RuntimeContext {
    val type: AbstractClassifierDefinition


    override fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        val method = type.definitions[name]
        require(method is Callable) {
            "Unrecogized method $name: $method"
        }
        return method.call(this, children, parameterContext)
    }
}