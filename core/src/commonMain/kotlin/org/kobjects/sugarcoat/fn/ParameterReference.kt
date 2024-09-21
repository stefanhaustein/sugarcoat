package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression

data class ParameterReference(
    val name: String,
    val value: Expression
) {
    override fun toString() = if (name.isEmpty()) value.toString() else "$name = $value"
}