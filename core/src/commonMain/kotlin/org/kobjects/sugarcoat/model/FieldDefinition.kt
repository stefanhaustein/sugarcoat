package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.base.Type

data class FieldDefinition(
    val parent: StructDefinition,
    val name: String,
    val type: Type,
    val defaultExpression: Expression?
)
