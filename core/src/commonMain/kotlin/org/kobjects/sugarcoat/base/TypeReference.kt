package org.kobjects.sugarcoat.base


class TypeReference(
    val namespace: Namespace,
    val name: String,
    val genericParameters: List<String>
) : Type {
    override fun resolve() = (namespace.resolveOrNull(name) as Type).resolve()

    override fun toString() = name
}