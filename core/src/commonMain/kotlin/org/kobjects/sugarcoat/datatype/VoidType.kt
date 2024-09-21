package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterReference
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.RuntimeContext

object VoidType : NativeType("Void") {


    object Instance : NativeInstance() {
        override val type: NativeType
            get() = VoidType
    }
}