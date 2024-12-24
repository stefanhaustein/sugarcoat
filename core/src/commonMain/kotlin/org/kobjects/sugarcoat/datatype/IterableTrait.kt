package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.DelegateToImpl
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.model.TraitDefinition
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

class IterableTrait(
    val elementType: Type
) : TraitDefinition(RootContext, RootContext, "Iterable") {

    init {
        addChild(DelegateToImpl(
            this, null, "iterator", FunctionType(IteratorTrait(elementType))))
    }

    override fun resolveGenerics(state: GenericTypeResolver): Type {
        return IterableTrait(elementType.resolveGenerics(state))
    }


    override fun matchImpl(
        other: Type,
        genericTypeResolver: GenericTypeResolver?,
        lazyMessage: () -> String
    ) {
        require(other is IterableTrait, lazyMessage)
        elementType.match(other.elementType, genericTypeResolver, lazyMessage)
    }

}