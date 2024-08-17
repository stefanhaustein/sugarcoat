package org.kobjects.sugarcoat

data class ParameterReference(
    val name: String,
    val value: Evaluable
) {
    override fun toString() = if (name.isEmpty()) value.toString() else "$name = $value"
}