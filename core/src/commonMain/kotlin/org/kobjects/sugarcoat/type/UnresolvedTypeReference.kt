package org.kobjects.sugarcoat.type

import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.parser.Position


class UnresolvedTypeReference(
    val position: Position,
    val name: String,
    val genericParameters: List<Type>
) : Type {

    override fun resolveType(context: Classifier): Type {
        val classifier = context.resolveSymbol(name)

        val parameters = genericParameters.map { it.resolveType(context) }

        return classifier.resolveGenericParameters(parameters)
    }

    override fun toString() = "Unresolved:$name"
}