package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.fn.ParameterDefinition

object I64Type : NativeType("I64", RootContext) {

    init {
        addNativeFunction(I64Type, "parse", ParameterDefinition("s", StringType)) { it.list[0].toString().toLong() }

        addNativeMethod(StringType, "toString") { it.i64(0).toString() }
        addNativeMethod(F64Type, "toF64") { it.i64(0).toDouble() }
        addNativeMethod(I64Type, "0-", ) {  -it.i64(0) }

        addNativeMethod(I64Type, "*", ParameterDefinition("other", I64Type)) { it.i64(0) * it.i64(1) }
        addNativeMethod(I64Type, "%", ParameterDefinition("other", I64Type)) { it.i64(0) % it.i64(1) }
        addNativeMethod(I64Type, "/", ParameterDefinition("other", I64Type)) { it.i64(0) / it.i64(1) }
        addNativeMethod(I64Type, "+", ParameterDefinition("other", I64Type)) { it.i64(0) + it.i64(1) }
        addNativeMethod(I64Type, "-", ParameterDefinition("other", I64Type)) { it.i64(0) - it.i64(1) }

        addNativeMethod(BoolType, "==", ParameterDefinition("other", I64Type)) { it.i64(0) == it.i64(1) }
        addNativeMethod(BoolType, "!=", ParameterDefinition("other", I64Type)) { it.i64(0) != it.i64(1) }
        addNativeMethod(BoolType, "<=", ParameterDefinition("other", I64Type)) { it.i64(0) <= it.i64(1) }
        addNativeMethod(BoolType, ">=", ParameterDefinition("other", I64Type)) { it.i64(0) >= it.i64(1) }
        addNativeMethod(BoolType, "<", ParameterDefinition("other", I64Type)) { it.i64(0) < it.i64(1) }
        addNativeMethod(BoolType, ">", ParameterDefinition("other", I64Type)) { it.i64(0) > it.i64(1) }
    }

}