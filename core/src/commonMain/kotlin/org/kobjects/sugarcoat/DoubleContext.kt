package org.kobjects.sugarcoat

import kotlin.math.pow

class DoubleContext(val value: Double) : RuntimeContext {

    override fun evalSymbol(
        name: String,
        children: List<Parameter>,
        parameterContext: RuntimeContext
    ): Any = when (name) {
        "+" -> children.fold(value) { acc, current -> acc + current.value.evalDouble(parameterContext) }
        "*" -> children.fold(value) { acc, current -> acc * current.value.evalDouble(parameterContext) }
        "/" -> children.fold(value) { acc, current -> acc / current.value.evalDouble(parameterContext) }
        "%" -> children.fold(value) { acc, current -> acc % current.value.evalDouble(parameterContext) }
        "-" -> if (children.isEmpty()) -value else children.fold(value) { acc, current -> acc - current.value.evalDouble(parameterContext) }
        "**" -> value.pow(children.first().value.evalDouble(parameterContext))
        "==" -> value == children.first().value.evalDouble(parameterContext)
        "!=" -> value != children.first().value.evalDouble(parameterContext)
        else -> throw UnsupportedOperationException("Method $name unsupported for boolean values.")
    }
}