package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.model.RootContext

object BoolType : NativeType( "Bool", RootContext) {

    init {
        addNativeMethod(BoolType, "!") { !it.bool(0)  }
        addNativeMethod(StringType, "toString") { it.bool(0).toString()  }
        addNativeMethod(BoolType, "==", ParameterDefinition("other", BoolType)) { it.bool(0) == it.bool(1) }
        addNativeMethod(BoolType, "!=", ParameterDefinition("other", BoolType)) { it.bool(0) != it.bool(1) }

        // TODO: lambda for short-circuit
        addNativeMethod(BoolType, "&&", ParameterDefinition("other", BoolType)) { it.bool(0) && it.bool(1) }
        addNativeMethod(BoolType, "||", ParameterDefinition("other", BoolType)) { it.bool(0) || it.bool(1) }


    }



}