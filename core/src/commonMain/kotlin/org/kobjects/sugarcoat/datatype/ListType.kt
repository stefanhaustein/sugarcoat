package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.type.GenericTypeResolverState
import org.kobjects.sugarcoat.type.Type

data class ListType(val elementType: Type) : NativeType("List") {

    override fun resolveGenerics(state: GenericTypeResolverState, expected: Type?): Type? {
        val expectedElementType = if (expected == null) null else {
            require(expected is ListType) {
                "${state.errorPrefix()}: List type expected; got $expected"
            }
            expected.elementType
        }
        val resolvedElementType = elementType.resolveGenerics(state, expectedElementType)
        if (resolvedElementType == null) {
            return null
        }
        return ListType(resolvedElementType)
    }

}