package org.kobjects.sugarcoat.type

import org.kobjects.sugarcoat.model.Classifier


class TypeReference(
    val namespace: Classifier,
    val name: String,
    val genericParameters: List<String>
) : Type {

    override fun resolve(context: Classifier): Type = namespace.resolveSymbol(name) as Type

    override fun toString() = name
}