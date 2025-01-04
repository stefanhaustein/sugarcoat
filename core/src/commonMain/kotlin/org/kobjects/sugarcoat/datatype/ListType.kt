package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.model.ImplInstance
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.type.GenericType


object ListType : NativeType("List", RootContext, listOf(GenericType("E"))) {

    init {
        addNativeMethod(typeParameters[0], "[]", ParameterDefinition("index", I64Type)) {
            (it.list[0] as List<Any>)[it.i64(1).toInt()]
        }
        addNativeMethod(I64Type, "size") {
            (it.list[0] as List<Any>).size.toLong()
        }

        addNativeFunction(this, "create", ParameterDefinition("values", typeParameters[0], true)) {
            it.list[0] as List<Any>
        }

        addNativeFunction(IteratorTrait.typed(typeParameters[0]), "iterator") {
           ImplInstance(NativeIterator.impl, (it.list[0] as List<Any>).iterator())
        }

        addImpl(IterableTrait.typed(typeParameters[0]))
    }



}