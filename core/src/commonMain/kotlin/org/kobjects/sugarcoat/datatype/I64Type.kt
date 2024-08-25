package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.ResolvedType
import org.kobjects.sugarcoat.runtime.RuntimeContext

class I64Type : ResolvedType {


    class Instance(val value: Long) : RuntimeContext {
        override fun evalSymbol(
            name: String,
            children: List<ParameterReference>,
            parameterContext: RuntimeContext
        ): RuntimeContext = when (name) {
            "+" -> Instance(children.fold(value) { acc, current -> acc + current.value.evalLong(parameterContext) })
            "*" -> Instance(children.fold(value) { acc, current -> acc * current.value.evalLong(parameterContext) })
            "/" -> Instance(children.fold(value) { acc, current -> acc / current.value.evalLong(parameterContext) })
            "%" -> Instance(children.fold(value) { acc, current -> acc % current.value.evalLong(parameterContext) })
            "-" -> Instance(if (children.isEmpty()) -value else children.fold(value) { acc, current -> acc - current.value.evalLong(parameterContext) })
            "==" -> BoolType.Instance(value == children.first().value.evalLong(parameterContext))
            "!=" -> BoolType.Instance(value != children.first().value.evalLong(parameterContext))
            else -> throw UnsupportedOperationException("Method $name unsupported for long values.")
        }

        override fun toString() = value.toString()

        override fun equals(other: Any?) = other is I64Type.Instance && other.value == value

        override fun hashCode() = value.hashCode()
    }
}