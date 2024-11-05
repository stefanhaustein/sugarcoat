package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.type.GenericTypeResolverState
import kotlin.math.exp

data class PairType(val firstType: Type, val secondType: Type) : Type {


    override fun resolve(context: Classifier): Type {
        return PairType(firstType.resolve(context), secondType.resolve(context))
    }

    override fun resolveGenerics(state: GenericTypeResolverState, expected: Type?): Type? {
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