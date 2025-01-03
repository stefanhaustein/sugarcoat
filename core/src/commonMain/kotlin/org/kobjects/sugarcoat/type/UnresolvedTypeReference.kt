package org.kobjects.sugarcoat.type

import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.Namespace
import org.kobjects.sugarcoat.parser.Position


class UnresolvedTypeReference(
    val position: Position,
    val name: String,
    val genericParameters: List<Type>
) : Type {

    override fun resolveType(context: Namespace): Type {
        if (context is Classifier) {
            val genericType =
                context.typeParameters.firstOrNull() { it is GenericType && it.name == name }
            if (genericType != null) {
                return genericType
            }
        }
        val classifier = context.resolveSymbol(name) { "$position" }
        require (classifier is Classifier) {
            "$position: $classifier is not a type."
        }

        val parameters = genericParameters.map { it.resolveType(context) }

        return classifier.typed(*parameters.toTypedArray())
    }

    override fun toString() = "Unresolved:$name"
}