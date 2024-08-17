package org.kobjects.sugarcoat.function

data class ParameterDeclaration(
    val name: String,
    val repeated: Boolean = false,
    val resolve: Boolean = false,
) {
    override fun toString() = name
}