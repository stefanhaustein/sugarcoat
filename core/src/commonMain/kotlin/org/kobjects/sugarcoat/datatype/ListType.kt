package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.model.ImplDefinition
import org.kobjects.sugarcoat.model.ImplInstance
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

data class ListType(val elementType: Type) : NativeType("List", RootContext) {

    init {
        addNativeMethod(elementType, "[]", ParameterDefinition("index", I64Type)) {
            (it.list[0] as List<Any>)[it.i64(1).toInt()]
        }
        addNativeMethod(I64Type, "size") {
            (it.list[0] as List<Any>).size.toLong()
        }

        addNativeFunction(this, "create", ParameterDefinition("values", elementType, true)) {
            it.list[0] as List<Any>
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
        require(other is ListType, lazyMessage)
        elementType.match(other.elementType, genericTypeResolver, lazyMessage)
    }

    override fun resolveGenerics(state: GenericTypeResolver): Type {
        return ListType(elementType.resolveGenerics(state))
    }


    override fun resolveGenericParameters(resolvedTypes: List<Type>): Type {
        require(resolvedTypes.size == 1) {
            "List requires 1 generic parameter. Provided: $resolvedTypes"
        }
        return ListType(resolvedTypes[0])
    }

    fun getTypeParameters(): Set<GenericType> = elementType.getGenericTypes()
}