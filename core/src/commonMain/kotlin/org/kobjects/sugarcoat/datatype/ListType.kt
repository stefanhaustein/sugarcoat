package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.type.GenericTypeResolverState
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
    }


    override fun resolveGenerics(state: GenericTypeResolverState, expected: Type?): Type? {
        val expectedElementType = if (expected == null) null else {
            require(expected is ListType) {
                "${state.errorPrefix()}: List type expected; got $expected (${expected::class})"
            }
            expected.elementType
        }
        val resolvedElementType = elementType.resolveGenerics(state, expectedElementType)
        if (resolvedElementType == null) {
            return null
        }
        return ListType(resolvedElementType)
    }


    override fun resolveGenericParameters(resolvedTypes: List<Type>): Type {
        require(resolvedTypes.size == 1) {
            "List requires 1 generic parameter. Provided: $resolvedTypes"
        }
        return ListType(resolvedTypes[0])
    }
}