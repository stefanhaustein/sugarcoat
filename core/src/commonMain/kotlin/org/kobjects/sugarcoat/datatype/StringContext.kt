package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.runtime.RuntimeContext

class StringContext(val value: String) : RuntimeContext {

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

    override fun equals(other: Any?) = other is StringContext && other.value == value

    override fun hashCode() = value.hashCode()

}