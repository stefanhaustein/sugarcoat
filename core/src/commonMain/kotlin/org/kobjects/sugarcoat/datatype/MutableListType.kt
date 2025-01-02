package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.model.ImplDefinition
import org.kobjects.sugarcoat.model.ImplInstance
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

data class MutableListType(val elementType: Type) : NativeType("MutableList", RootContext) {

    init {
        addNativeMethod(elementType, "[]", ParameterDefinition("index", I64Type)) {
            (it.list[0] as List<Any>)[it.i64(1).toInt()]
        }
        addNativeMethod(I64Type, "size") {
            (it.list[0] as List<Any>).size.toLong()
        }
        addNativeMethod(I64Type, "add", ParameterDefinition("value", elementType)) {
            (it.list[0] as MutableList<Any>).add(it.list[1])
        }
        addNativeMethod(VoidType, "removeAt", ParameterDefinition("index", I64Type)) {
            (it.list[0] as MutableList<Any>).removeAt((it.list[1] as Long).toInt())
        }
        addNativeFunction(this, "create", ParameterDefinition("values", elementType, true)) {
            (it.list[0] as List<Any>).toMutableList()
        }

        val iteratorTrait = IteratorTrait(elementType)
        val nativeIterator = NativeIterator(elementType)
        addNativeFunction(iteratorTrait, "iterator") {
           ImplInstance(nativeIterator.impl, (it.list[0] as List<Any>).iterator())
        }
        addImpl(IterableTrait(elementType))
    }

    override fun matchImpl(
        other: Type,
        genericTypeResolver: GenericTypeResolver?,
        lazyMessage: () -> String
    ) {
        require(other is MutableListType, lazyMessage)
        elementType.match(other.elementType, genericTypeResolver, lazyMessage)
    }

    override fun resolveGenerics(state: GenericTypeResolver): Type {
        return MutableListType(elementType.resolveGenerics(state))
    }


    override fun resolveGenericParameters(resolvedTypes: List<Type>): Type {
        require(resolvedTypes.size == 1) {
            "List requires 1 generic parameter. Provided: $resolvedTypes"
        }
        return MutableListType(resolvedTypes[0])
    }

    fun getTypeParameters(): Set<GenericType> = elementType.getGenericTypes()
}