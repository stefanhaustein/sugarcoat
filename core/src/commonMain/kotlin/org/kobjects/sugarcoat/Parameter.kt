package org.kobjects.sugarcoat

data class Parameter(
    val name: String,
    val value: Evaluable
) {
    override fun toString() = if (name.isEmpty()) value.toString() else "$name = $value"
}