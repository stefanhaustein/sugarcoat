package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.model.ImplDefinition
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.type.Type

class NativeIterator(elementType: Type) : NativeType("NativeIterator", RootContext) {

    val impl: ImplDefinition

    init {
        addNativeFunction(elementType, "next") {
            (it.list[0] as Iterator<Any>).next()
        }
        addNativeFunction(elementType, "hasNext") {
            (it.list[0] as Iterator<Any>).hasNext()
        }
        impl = addImpl(IteratorTrait(elementType))
    }
}