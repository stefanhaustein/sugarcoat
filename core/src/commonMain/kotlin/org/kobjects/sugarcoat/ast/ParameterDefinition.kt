package org.kobjects.sugarcoat.ast

data class ParameterDefinition(
    val name: String,
    val type: Type,
    val repeated: Boolean = false,
) {
    override fun toString() = "$name: $type"
}