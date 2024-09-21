package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.Type

class FunctionType(
    val parameterTypes: List<Type>,
    val returnType: Type
) : ResolvedType