package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.model.ImplDefinition
import org.kobjects.sugarcoat.model.RootContext

object StringType : NativeType("String", RootContext) {
    init {
        addNativeMethod(StringType, "+", ParameterDefinition("other", StringType)) { it.list[0].toString() + it.list[1] }

        addNativeMethod(ListType(StringType), "split", ParameterDefinition("by", StringType)) { it.list[0].toString().split(Regex.fromLiteral(it.list[1].toString()))}

        addNativeMethod(StringType, "toString") { it.list[0].toString() }

        addImpl(ToStringTrait)
    }
}
