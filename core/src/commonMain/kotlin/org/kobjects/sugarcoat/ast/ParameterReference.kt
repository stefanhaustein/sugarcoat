package org.kobjects.sugarcoat.ast

data class ParameterReference(
    val name: String,
    val value: Expression
) {
    override fun toString() = if (name.isEmpty()) value.toString() else "$name = $value"
}