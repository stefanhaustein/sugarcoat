package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.RuntimeContext
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.ParameterReference

class StructInstance(
    val parentContext: RuntimeContext,
    override val type: StructDefinition
) : Instance {
    val fields = mutableMapOf<String, RuntimeContext>()

    override fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        val resolvedField = fields[name]
        if (resolvedField != null) {
            require(children.isEmpty()) {
                "Unexpected parameters for $name"
            }
            return resolvedField
        }
        val method = type.definitions[name]
        if (method != null) {
            require(method is Callable) {
                "Unrecogized method $name: $method"
            }
            return method.call(this, children, parameterContext)
        }
        return parentContext.evalSymbol(name, children, parameterContext)
    }

    override fun toString() = "StructInstance; def: $type; fields: $fields"
}