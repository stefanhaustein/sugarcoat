package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.model.Namespace
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver

data class PairType(val firstType: Type, val secondType: Type) : NativeType("Pair", RootContext) {

    init {
        addNativeFunction(this, "create", ParameterDefinition("first", firstType), ParameterDefinition("second", secondType)) {
            Pair(it.list[0], it.list[1])
        }
    }

    override fun matchImpl(other: Type, genericTypeResolver: GenericTypeResolver?, lazyMessage: () -> String) {
        require(other is PairType, lazyMessage)
        firstType.match(other.firstType, genericTypeResolver, lazyMessage)
        secondType.match(other.secondType, genericTypeResolver, lazyMessage)
    }

    override fun resolveGenerics(state: GenericTypeResolver): Type {

        val firstResolved = firstType.resolveGenerics(state)
        val secondResolved = secondType.resolveGenerics(state)

        return  PairType(firstResolved, secondResolved)
    }



}