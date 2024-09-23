package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Scope
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.ast.ParameterReference

class StructInstance(
    val parentContext: Scope,
    override val type: StructDefinition
) : Instance {
    val fields = mutableMapOf<String, Scope>()

    override fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: Scope
    ): Scope {
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
                "Unrecognized method $name: $method"
            }
            return method.call(this, children, parameterContext)
        }
        return parentContext.evalSymbol(name, children, parameterContext)
    }

    override fun toString() = "StructInstance; def: $type; fields: $fields"
}