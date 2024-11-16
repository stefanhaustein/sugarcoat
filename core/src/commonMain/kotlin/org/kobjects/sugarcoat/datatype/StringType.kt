package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.model.RootContext

object StringType : NativeType("String", RootContext) {
    init {
        addNativeMethod(StringType, "+", ParameterDefinition("other", StringType)) { it.list[0].toString() + it.list[1] }
    }
}
