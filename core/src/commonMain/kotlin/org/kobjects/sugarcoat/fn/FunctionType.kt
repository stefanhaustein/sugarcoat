package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.Type

data class FunctionType(
    val returnType: Type,
    val parameterTypes: List<Type>
) : ResolvedType {
    constructor(returnType: Type, vararg parameterTypes: Type) : this(returnType, parameterTypes.asList())
}