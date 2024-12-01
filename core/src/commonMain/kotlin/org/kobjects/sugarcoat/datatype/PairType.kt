package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver

data class PairType(val firstType: Type, val secondType: Type) : Type {

    override fun matchImpl(other: Type, genericTypeResolver: GenericTypeResolver, lazyMessage: () -> String): PairType {
        require(other is PairType, lazyMessage)
        return PairType(
            firstType.match(other.firstType, genericTypeResolver, lazyMessage),
            secondType.match(other.secondType, genericTypeResolver, lazyMessage))
    }

    override fun resolveType(context: Classifier): Type {
        return PairType(firstType.resolveType(context), secondType.resolveType(context))
    }

    override fun resolveGenerics(state: GenericTypeResolver, expected: Type?): Type? {
        if (this == expected) {
            return this
        }

        if (expected !is PairType?) {
            return null
        }

        val firstResolved = firstType.resolveGenerics(state, expected?.firstType)
        val secondResolved = secondType.resolveGenerics(state, expected?.secondType)

        return if (firstResolved != null && secondResolved != null) PairType(firstResolved, secondResolved) else null
    }

}