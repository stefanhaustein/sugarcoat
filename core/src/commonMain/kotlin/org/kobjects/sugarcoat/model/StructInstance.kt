package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.ast.ParameterReference

class StructInstance(
    override val type: StructDefinition
) : Instance {
    val fields = mutableMapOf<String, Any>()

   // override fun getField(name: String) = fields[name]

    override fun toString() = "StructInstance; def: $type; fields: $fields"
}