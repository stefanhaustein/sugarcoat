package org.kobjects.sugarcoat.runtime

import org.kobjects.sugarcoat.ast.Callable
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.StructDefinition

class StructInstance(
    val parentContext: RuntimeContext,
    val definition: StructDefinition
) : RuntimeContext {
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
        val method = definition.definitions[name]
        if (method != null) {
            require(method is Callable) {
                "Unrecogized method $name: $method"
            }
            return method.call(this, children, parameterContext)
        }
        return parentContext.evalSymbol(name, children, parameterContext)
    }

    override fun toString() = "StructInstance; def: $definition; fields: $fields"
}