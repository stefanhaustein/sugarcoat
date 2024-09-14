package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.ast.AbstractClassifierDefinition
import org.kobjects.sugarcoat.ast.Definition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.runtime.RuntimeContext

object BoolType : AbstractClassifierDefinition(null, "Bool") {


    class Instance(val value: Boolean) : RuntimeContext {

        override fun evalSymbol(
            name: String,
            children: List<ParameterReference>,
            parameterContext: RuntimeContext
        ): RuntimeContext {
            when (name) {
                else -> throw UnsupportedOperationException("Method $name unsupported for boolean values.")
            }
        }
    }


}