package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.type.Type

data class FunctionType(
    val returnType: Type,
    val parameterTypes: List<ParameterDefinition>
) : Type {
    constructor(returnType: Type, vararg parameterTypes: ParameterDefinition) : this(returnType, parameterTypes.asList())
}