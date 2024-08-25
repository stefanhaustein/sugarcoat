package org.kobjects.sugarcoat.ast

class FunctionType(
    val parameterTypes: List<Type>,
    val returnType: Type
) : ResolvedType