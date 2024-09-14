package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.ast.AbstractClassifierDefinition
import org.kobjects.sugarcoat.ast.Definition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.runtime.RuntimeContext

object BoolType : NativeType( "Bool") {


    data class Instance(val value: Boolean) : NativeInstance() {
        override val type: NativeType
            get() = TODO("Not yet implemented")

        override fun toString() = value.toString()
    }

}