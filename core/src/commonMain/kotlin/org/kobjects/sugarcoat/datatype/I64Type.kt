package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.fn.ParameterDefinition

object I64Type : NativeType("I64", RootContext) {

    init {
        addNativeMethod(I64Type, "%", ParameterDefinition("other", I64Type)) { it.i64(0) % it.i64(1) }
        addNativeMethod(I64Type, "+", ParameterDefinition("other", I64Type)) { it.i64(0) + it.i64(1) }
        addNativeMethod(I64Type, "-", ParameterDefinition("other", I64Type)) { it.i64(0) - it.i64(1) }
        addNativeMethod(I64Type, "//", ParameterDefinition("other", I64Type)) { it.i64(0) / it.i64(1) }
        addNativeMethod(BoolType, "==", ParameterDefinition("other", I64Type)) { it.i64(0) == it.i64(1) }
        addNativeMethod(BoolType, "!=", ParameterDefinition("other", I64Type)) { it.i64(0) == it.i64(1) }
    }

}