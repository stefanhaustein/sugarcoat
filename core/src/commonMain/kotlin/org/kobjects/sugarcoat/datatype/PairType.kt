package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.model.Classifier

class PairType(val firstType: Type, val secondType:Type) : Type {


    override fun resolve(context: Classifier): Type {
        return PairType(firstType.resolve(context), secondType.resolve(context))
    }
}