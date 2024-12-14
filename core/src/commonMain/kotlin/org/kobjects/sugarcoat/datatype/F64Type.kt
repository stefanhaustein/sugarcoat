package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.model.RootContext
import org.kobjects.sugarcoat.fn.ParameterDefinition
import kotlin.math.pow

object F64Type : NativeType("F64", RootContext) {

    init {
        addNativeFunction(F64Type, "parse", ParameterDefinition("s", StringType)) { it.list[0].toString().toDouble() }

        addNativeMethod(F64Type, "0-", ) {  -it.f64(0) }
        addNativeMethod(I64Type, "toI64", ) {  it.f64(0).toLong() }
        addNativeMethod(StringType, "toString", ) {  it.f64(0).toString() }

        addNativeMethod(F64Type, "**", ParameterDefinition("other", F64Type)) { it.f64(0).pow(it.f64(1)) }
        addNativeMethod(F64Type, "*", ParameterDefinition("other", F64Type)) { it.f64(0) * it.f64(1) }
        addNativeMethod(F64Type, "/", ParameterDefinition("other", F64Type)) { it.f64(0) / it.f64(1) }
        addNativeMethod(F64Type, "%", ParameterDefinition("other", F64Type)) { it.f64(0) % it.f64(1) }
        addNativeMethod(F64Type, "+", ParameterDefinition("other", F64Type)) { it.f64(0) + it.f64(1) }
        addNativeMethod(F64Type, "-", ParameterDefinition("other", F64Type)) { it.f64(0) - it.f64(1) }

        addNativeMethod(BoolType, "==", ParameterDefinition("other", F64Type)) { it.f64(0) == it.f64(1) }
        addNativeMethod(BoolType, "!=", ParameterDefinition("other", F64Type)) { it.f64(0) != it.f64(1) }
        addNativeMethod(BoolType, "<=", ParameterDefinition("other", F64Type)) { it.f64(0) <= it.f64(1) }
        addNativeMethod(BoolType, ">=", ParameterDefinition("other", F64Type)) { it.f64(0) >= it.f64(1) }
        addNativeMethod(BoolType, "<", ParameterDefinition("other", F64Type)) { it.f64(0) < it.f64(1) }
        addNativeMethod(BoolType, ">", ParameterDefinition("other", F64Type)) { it.f64(0) > it.f64(1) }
    }

}