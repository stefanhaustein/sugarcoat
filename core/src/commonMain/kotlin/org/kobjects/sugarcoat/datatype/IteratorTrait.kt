package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.DelegateToImpl
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.model.TraitDefinition
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

object IteratorTrait : TraitDefinition(RootContext, RootContext, "Iterator", listOf(GenericType("E"))) {

    init {
        addChild(DelegateToImpl(this, null, "next", FunctionType(this, typeParameters[0])))
        addChild(DelegateToImpl(this, null, "hasNext", FunctionType(this, BoolType)))
    }


}