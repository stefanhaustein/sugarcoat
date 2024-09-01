package org.kobjects.sugarcoat.runtime

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.StructDefinition

class StructInstance(
    val definition: StructDefinition
) : RuntimeContext {
    val fields = mutableMapOf<String, RuntimeContext>()

    override fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        val resolved = fields[name] ?: throw IllegalArgumentException("Unresolved field '$name'")
        require(children.isEmpty()) {
            "Unexpected parameters for $name"
        }
        return resolved
    }

    override fun toString() = "StructInstance; def: $definition; fields: $fields"
}