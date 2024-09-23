package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Scope

object StringType : NativeType("String") {


    data class Instance(val value: String) : NativeInstance() {
        override val type: NativeType
            get() = StringType


        override fun toString() = value
    }
}