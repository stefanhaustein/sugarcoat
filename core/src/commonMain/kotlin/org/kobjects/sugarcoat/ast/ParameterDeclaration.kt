package org.kobjects.sugarcoat.ast

data class ParameterDeclaration(
    val name: String,
    val repeated: Boolean = false,
    val resolve: Boolean = true,
) {
    override fun toString() = name
}