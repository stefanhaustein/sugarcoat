package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.Type

abstract class Classifier(
    parent: Namespace?,
    name: String,
    override val genericTypes: List<GenericType> = emptyList(),
    fallback: Namespace? = null
) : Namespace(parent, name, fallback), Type {



    open fun resolveGenericParameters(resolvedTypes: List<Type>): Type {
        require(resolvedTypes.size == genericTypes.size) {
            "${genericTypes.size} types expected to resolve $genericTypes in $this, but got $resolvedTypes"
        }

        if (resolvedTypes.isEmpty()) {
            return this
        }

        val resolved = DegenerifiedClassifierProxy(this, resolvedTypes)
        return resolved
    }
}