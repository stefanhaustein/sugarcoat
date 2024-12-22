package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.DelegateToImpl
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.model.TraitDefinition
import org.kobjects.sugarcoat.type.Type

class IteratorTrait(elementType: Type) : TraitDefinition(RootContext, RootContext, "Iterator") {

    init {
        addChild(DelegateToImpl(this, null, "next", FunctionType(elementType)))
        addChild(DelegateToImpl(this, null, "hasNext", FunctionType(BoolType)))
    }

}