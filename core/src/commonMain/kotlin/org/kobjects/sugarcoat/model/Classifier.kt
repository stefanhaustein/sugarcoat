package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.fn.AbstractFunctionDefinition
import org.kobjects.sugarcoat.fn.DeGenerifiedFunctionProxy
import org.kobjects.sugarcoat.fn.DelegateToImpl
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

abstract class Classifier(
    parent: Namespace?,
    name: String,
    val typeParameters: List<Type> = emptyList(),
    fallback: Namespace? = null
) : Namespace(parent, name, fallback), Type {

    // The cache reduces overhead and -- more importantly -- breaks infinite resolution recursions.
    val deGenerified = mutableMapOf<List<Type>, Classifier>()

    open val constructorName:  String
        get() = ""

    override val raw: Classifier
        get() = this

    open fun typed(vararg resolvedTypes: Type): Classifier {
        require(resolvedTypes.size == typeParameters.size) {
            "${typeParameters.size} types expected to resolve $typeParameters in $this, but got $resolvedTypes"
        }

        if (resolvedTypes.isEmpty()) {
            return this
        }

        val genericTypeResolver = GenericTypeResolver()

        for (i in typeParameters.indices) {
            genericTypeResolver.map[typeParameters[i] as GenericType] = resolvedTypes[i]
        }

        return resolveGenerics(genericTypeResolver)
    }

    override fun resolveGenerics(state: GenericTypeResolver): Classifier {
        val resolvedTypeParameters = state.resolveAll(typeParameters)
        if (resolvedTypeParameters == typeParameters) {
            return this
        }
        var result = deGenerified[resolvedTypeParameters]
        return if (result != null) result else {
            val proxy = when (this) {
                is TraitDefinition -> TraitDefinition(
                    parent!!,
                    fallback!!,
                    name + resolvedTypeParameters,
                    resolvedTypeParameters,
                    raw
                )
                else -> DeGenerifiedClassifierProxy(raw, resolvedTypeParameters)
            }
            deGenerified[resolvedTypeParameters] = proxy
            proxy.populateDeGenerified()
            proxy
        }
    }

    private fun populateDeGenerified() {
        val genericTypeResolver = GenericTypeResolver()
        for (i in raw.typeParameters.indices) {
            genericTypeResolver.map[raw.typeParameters[i] as GenericType] = typeParameters[i]
        }

        for (member in raw.definitions.values) {
            if (member is DelegateToImpl) {
                val resolved = DelegateToImpl(this as TraitDefinition, member.fallback, member.name, member.type.resolveGenerics(genericTypeResolver))
                addChild(resolved)
            } else if (member is AbstractFunctionDefinition) {
                val resolved = DeGenerifiedFunctionProxy.create(this, member, genericTypeResolver)
                println("Resolved function: $resolved")
                addChild(resolved)
            }
        }
    }


    override fun matchImpl(
        other: Type,
        genericTypeResolver: GenericTypeResolver?,
        lazyMessage: () -> String
    ) {
        require(other is Classifier && other.raw == raw, lazyMessage)
        for (i in typeParameters.indices) {
            typeParameters[i].match(other.typeParameters[i], genericTypeResolver, lazyMessage)
        }

    }
}