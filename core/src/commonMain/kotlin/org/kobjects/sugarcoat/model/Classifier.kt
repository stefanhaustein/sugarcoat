package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

abstract class Classifier(
    parent: Namespace?,
    name: String,
    override val typeParameters: List<GenericType> = emptyList(),
    fallback: Namespace? = null
) : Namespace(parent, name, fallback), Type {



    open fun resolveGenericParameters(resolvedTypes: List<Type>): Type {
        require(resolvedTypes.size == typeParameters.size) {
            "${typeParameters.size} types expected to resolve $typeParameters in $this, but got $resolvedTypes"
        }

        if (resolvedTypes.isEmpty()) {
            return this
        }

        val resolved = DegenerifiedClassifierProxy(this, resolvedTypes)
        return resolved
    }

    override fun matchImpl(
        other: Type,
        genericTypeResolver: GenericTypeResolver?,
        lazyMessage: () -> String
    ) {

    }
}