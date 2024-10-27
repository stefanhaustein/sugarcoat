package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.Type

data class FunctionType(
    val returnType: Type,
    val parameterTypes: List<ParameterDefinition>
) : ResolvedType {
    constructor(returnType: Type, vararg parameterTypes: ParameterDefinition) : this(returnType, parameterTypes.asList())
}