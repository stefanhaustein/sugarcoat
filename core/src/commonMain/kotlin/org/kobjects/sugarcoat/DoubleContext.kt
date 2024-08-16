package org.kobjects.sugarcoat

import kotlin.math.pow

class DoubleContext(val value: Double) : RuntimeContext {

    override fun evalSymbol(
        name: String,
        children: List<Parameter>,
        parameterContext: RuntimeContext
    ): RuntimeContext = when (name) {
        "+" -> DoubleContext(children.fold(value) { acc, current -> acc + current.value.evalDouble(parameterContext) })
        "*" -> DoubleContext(children.fold(value) { acc, current -> acc * current.value.evalDouble(parameterContext) })
        "/" -> DoubleContext(children.fold(value) { acc, current -> acc / current.value.evalDouble(parameterContext) })
        "%" -> DoubleContext(children.fold(value) { acc, current -> acc % current.value.evalDouble(parameterContext) })
        "-" -> DoubleContext(if (children.isEmpty()) -value else children.fold(value) { acc, current -> acc - current.value.evalDouble(parameterContext) })
        "**" -> DoubleContext(value.pow(children.first().value.evalDouble(parameterContext)))
        "==" -> BooleanContext(value == children.first().value.evalDouble(parameterContext))
        "!=" -> BooleanContext(value != children.first().value.evalDouble(parameterContext))
        else -> throw UnsupportedOperationException("Method $name unsupported for boolean values.")
    }

    override fun toString() = value.toString()

    override fun equals(other: Any?) = other is DoubleContext && other.value == value

    override fun hashCode() = value.hashCode()
}