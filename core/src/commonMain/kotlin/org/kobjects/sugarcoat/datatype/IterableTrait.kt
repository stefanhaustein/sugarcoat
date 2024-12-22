package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.DelegateToImpl
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.model.TraitDefinition
import org.kobjects.sugarcoat.type.Type

class IterableTrait(
    val elementType: Type
) : TraitDefinition(RootContext, RootContext, "Iterable") {

    init {
        addChild(DelegateToImpl(
            this, null, "iterator", FunctionType(IteratorTrait(elementType))))
    }
}