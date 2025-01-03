package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

abstract class Classifier(
    parent: Namespace?,
    name: String,
    val typeParameters: List<Type> = emptyList(),
    fallback: Namespace? = null
) : Namespace(parent, name, fallback), Type {

    val deGenerified = mutableMapOf<List<Type>, DeGenerifiedClassifierProxy>()

    open val original: Classifier
        get() = this

    open val constructorName:  String
        get() = ""


    open fun typed(vararg resolvedTypes: Type): Type {
        require(resolvedTypes.size == typeParameters.size) {
            "${typeParameters.size} types expected to resolve $typeParameters in $this, but got $resolvedTypes"
        }

        if (resolvedTypes.isEmpty()) {
            return this
        }

        val genericTypeResolver = GenericTypeResolver()

        for (i in original.typeParameters.indices) {
            genericTypeResolver.map[original.typeParameters[i] as GenericType] = typeParameters[i]
        }

        return resolveGenerics(genericTypeResolver)
    }

    override fun resolveGenerics(state: GenericTypeResolver): Type {
        val resolvedTypeParameters = state.resolveAll(typeParameters)
        if (resolvedTypeParameters == typeParameters) {
            return this
        }
        return deGenerified.getOrPut(resolvedTypeParameters) {
            DeGenerifiedClassifierProxy(this, state)
        }
    }

    override fun matchImpl(
        other: Type,
        genericTypeResolver: GenericTypeResolver?,
        lazyMessage: () -> String
    ) {
        require(other is Classifier && other.original == original, lazyMessage)
        for (i in typeParameters.indices) {
            typeParameters[i].match(other.typeParameters[i], genericTypeResolver, lazyMessage)
        }

    }
}