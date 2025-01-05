package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.ImplDefinition
import org.kobjects.sugarcoat.model.ImplInstance
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

object MutableListType : NativeType("MutableList", RootContext, listOf(GenericType("E"))) {

    init {
        addNativeMethod(typeParameters[0], "[]", ParameterDefinition("index", I64Type)) {
            (it.list[0] as List<Any>)[it.i64(1).toInt()]
        }
        addNativeMethod(I64Type, "size") {
            (it.list[0] as List<Any>).size.toLong()
        }
        addNativeMethod(I64Type, "add", ParameterDefinition("value", typeParameters[0])) {
            (it.list[0] as MutableList<Any>).add(it.list[1])
        }
        addNativeMethod(VoidType, "removeAt", ParameterDefinition("index", I64Type)) {
            (it.list[0] as MutableList<Any>).removeAt((it.list[1] as Long).toInt())
        }
        addNativeFunction(this, "create", ParameterDefinition("values", typeParameters[0], true)) {
            (it.list[0] as List<Any>).toMutableList()
        }

        val iteratorTrait = IteratorTrait.typed(typeParameters[0])
        //val nativeIterator = NativeIterator(elementType)
        addNativeFunction(iteratorTrait, "iterator") {
           ImplInstance(NativeIterator.impl, (it.list[0] as List<Any>).iterator())
        }
        addImpl(IterableTrait.typed(typeParameters[0]))
    }

}