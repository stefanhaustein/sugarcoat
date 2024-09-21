package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Definition
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.base.Type

class FieldDefinition(
    override val parent: Definition,
    val type: Type,
    val defaultExpression: Expression?
) : Definition {
    override fun addDefinition(name: String, value: Definition) {
        throw IllegalArgumentException("Adding $value to fields is not supported.")
    }
}