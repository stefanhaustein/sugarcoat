package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.RuntimeContext

object StringType : NativeType("String") {


    data class Instance(val value: String) : NativeInstance() {
        override val type: NativeType
            get() = StringType

        override fun evalSymbol(
            name: String,
            children: List<ParameterReference>,
            parameterContext: RuntimeContext
        ): RuntimeContext {
            when (name) {
                else -> throw UnsupportedOperationException("Method $name unsupported for boolean values.")
            }
        }

        override fun toString() = value
    }
}