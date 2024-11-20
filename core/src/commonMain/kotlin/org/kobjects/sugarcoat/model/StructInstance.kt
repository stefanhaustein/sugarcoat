package org.kobjects.sugarcoat.model


class StructInstance(
    override val type: StructDefinition
) : Instance {
    val fields = mutableMapOf<String, Any>()

   // override fun getField(name: String) = fields[name]

    override fun toString() = "StructInstance; def: $type; fields: $fields"
}