package org.kobjects.sugarcoat.ast

class FieldDefinition(
    override val parent: Definition,
    val type: Type,
    val defaultExpression: Expression?
) : Definition {
    override fun addDefinition(name: String, value: Definition) {
        throw IllegalArgumentException("Adding $value to fields is not supported.")
    }
}