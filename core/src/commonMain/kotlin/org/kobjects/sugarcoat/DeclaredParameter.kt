package org.kobjects.sugarcoat

data class DeclaredParameter(
    val name: String,
    val repeated: Boolean = false,
    val resolve: Boolean = false,
) {
    override fun toString() = name
}