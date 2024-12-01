package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.type.Type

object AnyType: Type {
    override fun matches(other: Type) = true
}