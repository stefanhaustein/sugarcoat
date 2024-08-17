package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.ParameterReference
import org.kobjects.sugarcoat.RuntimeContext

class LongContext(val value: Long) : RuntimeContext {
    override fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext = when (name) {
        "+" -> LongContext(children.fold(value) { acc, current -> acc + current.value.evalLong(parameterContext) })
        "*" -> LongContext(children.fold(value) { acc, current -> acc * current.value.evalLong(parameterContext) })
        "/" -> LongContext(children.fold(value) { acc, current -> acc / current.value.evalLong(parameterContext) })
        "%" -> LongContext(children.fold(value) { acc, current -> acc % current.value.evalLong(parameterContext) })
        "-" -> LongContext(if (children.isEmpty()) -value else children.fold(value) { acc, current -> acc - current.value.evalLong(parameterContext) })
        "==" -> BooleanContext(value == children.first().value.evalLong(parameterContext))
        "!=" -> BooleanContext(value != children.first().value.evalLong(parameterContext))
        else -> throw UnsupportedOperationException("Method $name unsupported for long values.")
    }

    override fun toString() = value.toString()

    override fun equals(other: Any?) = other is LongContext && other.value == value

    override fun hashCode() = value.hashCode()
}