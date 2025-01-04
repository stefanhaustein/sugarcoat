package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.model.Namespace
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver

object PairType : NativeType("Pair", RootContext, listOf(GenericType("F"), GenericType("S"))) {

    init {
        addNativeFunction(this, "create", ParameterDefinition("first", typeParameters[0]), ParameterDefinition("second", typeParameters[1])) {
            Pair(it.list[0], it.list[1])
        }
    }
/*
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


*/
}