package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

object AnyType: Type {
    override fun matchImpl(other: Type, genericTypeResolver: GenericTypeResolver?, messagePrefix: () -> String) = Unit
}