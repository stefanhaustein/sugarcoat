package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.ResolvedType
import org.kobjects.sugarcoat.runtime.RuntimeContext
import kotlin.math.pow

class F64Type : ResolvedType {

    class Instance(val value: Double) : RuntimeContext {

        override fun evalSymbol(
            name: String,
            children: List<ParameterReference>,
            parameterContext: RuntimeContext
        ): RuntimeContext = when (name) {
            "+" -> Instance(children.fold(value) { acc, current -> acc + current.value.evalDouble(parameterContext) })
            "*" -> Instance(children.fold(value) { acc, current -> acc * current.value.evalDouble(parameterContext) })
            "/" -> Instance(children.fold(value) { acc, current -> acc / current.value.evalDouble(parameterContext) })
            "%" -> Instance(children.fold(value) { acc, current -> acc % current.value.evalDouble(parameterContext) })
            "-" -> Instance(if (children.isEmpty()) -value else children.fold(value) { acc, current -> acc - current.value.evalDouble(parameterContext) })
            "**" -> Instance(value.pow(children.first().value.evalDouble(parameterContext)))
            "==" -> BoolType.Instance(value == children.first().value.evalDouble(parameterContext))
            "!=" -> BoolType.Instance(value != children.first().value.evalDouble(parameterContext))
            else -> throw UnsupportedOperationException("Method $name unsupported for boolean values.")
        }

        override fun toString() = value.toString()

        override fun equals(other: Any?) = other is Instance && other.value == value

        override fun hashCode() = value.hashCode()
    }

}