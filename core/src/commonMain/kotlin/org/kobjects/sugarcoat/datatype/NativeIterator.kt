package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.model.ImplDefinition
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.Type

object NativeIterator : NativeType("NativeIterator", RootContext, listOf(GenericType("E"))) {

    val impl: ImplDefinition

    init {
        addNativeFunction(typeParameters[0], "next") {
            (it.list[0] as Iterator<Any>).next()
        }
        addNativeFunction(typeParameters[0], "hasNext") {
            (it.list[0] as Iterator<Any>).hasNext()
        }
        impl = addImpl(IteratorTrait.typed(typeParameters[0]))
    }
}