package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.base.Type

object AnyType: Type {
    override fun assignableFrom(other: Type) = true
}