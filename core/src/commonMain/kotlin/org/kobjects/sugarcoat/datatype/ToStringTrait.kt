package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.DelegateToImpl
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.model.TraitDefinition

object ToStringTrait : TraitDefinition(RootContext, RootContext, "ToString") {

    init {
        addChild(DelegateToImpl(this, null, "toString", FunctionType(StringType)))
    }

}