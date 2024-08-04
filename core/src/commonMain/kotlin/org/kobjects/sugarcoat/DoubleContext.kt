package org.kobjects.sugarcoat

import kotlin.math.pow

class DoubleContext(val value: Double) : RuntimeContext {

    override fun evalMethod(
        name: String,
        children: List<Parameter>,
        parameterContext: RuntimeContext
    ): Any = when (name) {
        "+" -> children.subList(1, children.size).fold(value) { acc, current -> acc + current.value.evalDouble(parameterContext) }
        "*" -> children.subList(1, children.size).fold(value) { acc, current -> acc * current.value.evalDouble(parameterContext) }
        "/" -> children.subList(1, children.size).fold(value) { acc, current -> acc / current.value.evalDouble(parameterContext) }
        "%" -> children.subList(1, children.size).fold(value) { acc, current -> acc % current.value.evalDouble(parameterContext) }
        "-" -> if (children.size == 1) -value else children.subList(1, children.size).fold(value) { acc, current -> acc - current.value.evalDouble(parameterContext) }
        "**" -> value.pow(children[1].value.evalDouble(parameterContext))
        "==" -> value == children[1].value.evalDouble(parameterContext)
        "!=" -> value != children[1].value.evalDouble(parameterContext)
        else -> throw UnsupportedOperationException("Method $name unsupported for boolean values.")
    }
}