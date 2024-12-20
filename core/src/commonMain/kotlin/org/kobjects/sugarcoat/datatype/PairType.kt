package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver

data class PairType(val firstType: Type, val secondType: Type) : Type {

    override fun matchImpl(other: Type, genericTypeResolver: GenericTypeResolver?, lazyMessage: () -> String) {
        require(other is PairType, lazyMessage)
        firstType.match(other.firstType, genericTypeResolver, lazyMessage)
        secondType.match(other.secondType, genericTypeResolver, lazyMessage)
    }

    override fun resolveType(context: Classifier): Type {
        return PairType(firstType.resolveType(context), secondType.resolveType(context))
    }

    override fun resolveGenerics(state: GenericTypeResolver): Type {

        val firstResolved = firstType.resolveGenerics(state)
        val secondResolved = secondType.resolveGenerics(state)

        return  PairType(firstResolved, secondResolved)
    }

    override fun getGenericTypes(): Set<GenericType> {
        val result = mutableSetOf<GenericType>()
        result.addAll(firstType.getGenericTypes())
        result.addAll(secondType.getGenericTypes())
        return result.toSet()
    }

}