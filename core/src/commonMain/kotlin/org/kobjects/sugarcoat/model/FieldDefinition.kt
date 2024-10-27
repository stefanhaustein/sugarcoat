package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.base.Type

data class FieldDefinition(
    val name: String,
    val type: Type?,
    val expression: Expression?
) {
    fun resolve(context: Classifier): FieldDefinition {
        val resolvedExpression = expression?.resolve(null)

        val resolvedType = type?.resolve(context) ?: resolvedExpression!!.getType()

        return FieldDefinition(name, resolvedType, resolvedExpression)
    }

}
