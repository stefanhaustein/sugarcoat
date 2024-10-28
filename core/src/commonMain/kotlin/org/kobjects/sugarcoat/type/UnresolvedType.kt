package org.kobjects.sugarcoat.type

import org.kobjects.sugarcoat.model.Classifier

data class UnresolvedType(val source: Any) : Type {


    override fun resolve(context: Classifier): Type {
        throw IllegalStateException("Can't resolve $this")
    }
}