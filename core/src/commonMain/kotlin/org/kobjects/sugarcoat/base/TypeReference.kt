package org.kobjects.sugarcoat.base

import org.kobjects.sugarcoat.model.Classifier


class TypeReference(
    val namespace: Classifier,
    val name: String,
    val genericParameters: List<String>
) : Type {
    override fun resolve() = (namespace.resolve(name) as Type).resolve()

    override fun resolve(context: Classifier): Type = resolve()

    override fun toString() = name
}