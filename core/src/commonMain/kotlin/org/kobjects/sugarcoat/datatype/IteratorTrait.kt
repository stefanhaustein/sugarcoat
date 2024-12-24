package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.DelegateToImpl
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.model.TraitDefinition
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

class IteratorTrait(val elementType: Type) : TraitDefinition(RootContext, RootContext, "Iterator") {

    init {
        addChild(DelegateToImpl(this, null, "next", FunctionType(elementType)))
        addChild(DelegateToImpl(this, null, "hasNext", FunctionType(BoolType)))
    }


    override fun resolveGenerics(state: GenericTypeResolver): Type {
        return IteratorTrait(elementType.resolveGenerics(state))
    }


    override fun matchImpl(
        other: Type,
        genericTypeResolver: GenericTypeResolver?,
        lazyMessage: () -> String
    ) {
        require(other is IteratorTrait, lazyMessage)
        elementType.match(other.elementType, genericTypeResolver, lazyMessage)
    }

}