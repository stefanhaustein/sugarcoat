package org.kobjects.sugarcoat.base

class TypeReference(
    val name: String,
    val genericParameters: List<String>
) : Type {
    override fun resolve() = throw UnsupportedOperationException()

    override fun toString() = name
}