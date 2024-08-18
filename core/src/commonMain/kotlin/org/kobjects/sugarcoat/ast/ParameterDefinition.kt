package org.kobjects.sugarcoat.ast

data class ParameterDefinition(
    val name: String,
    val repeated: Boolean = false,
    val resolve: Boolean = true,
) {
    override fun toString() = name
}