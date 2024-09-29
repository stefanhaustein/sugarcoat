package org.kobjects.sugarcoat.base

import org.kobjects.sugarcoat.ast.LambdaExpression
import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.SymbolExpression
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import kotlin.math.sqrt

object ControlStructures  {
    fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: LocalRuntimeContext
    ) = when (name) {
            "for" -> evalFor(children, parameterContext)
            "if" -> evalIf(children, parameterContext)

            "print" -> parameterContext.globalRuntimeContext.printFn(children.joinToString { it.value.eval(parameterContext).toString() })


            "sqrt" -> sqrt(children[0].value.evalDouble(parameterContext))

            "range" -> when (children.size) {
                1 ->
                    LongRange(
                        0,
                        children[0].value.evalLong(parameterContext) - 1

                )

                2 ->
                    LongRange(
                        children.first().value.evalLong(parameterContext),
                        children.last().value.evalLong(parameterContext) - 1

                )

                else -> throw IllegalArgumentException("2 or 3 parameter expected for range, but got ${children.size}")
            }

            "seq" -> children.fold<ParameterReference, Any>(Unit) { _, current ->
                current.value.eval(
                    parameterContext
                )
            }

            "=" -> {
                require(children.size == 2) { "Two parameters expected for assignment" }
                val target = (children.first() as LiteralExpression).value as String
                (parameterContext as LocalRuntimeContext).symbols[target] =
                    children.last().value.eval(parameterContext)
                Unit
            }

            "while" -> {
                require(children.size == 2) { "Two parameters expected for 'while'." }
                while (children[0].value.evalBoolean(parameterContext)) children[1].value.eval(
                    parameterContext
                )
                Unit
            }

            else -> throw IllegalStateException("Unrecognized symbol: '$name'")
        }


fun evalIf(children: List<ParameterReference>, parameterContext: LocalRuntimeContext): Any {
    if (children[0].value.evalBoolean(parameterContext)) {
        return children[1].value.eval(parameterContext)
    }
    for (i in 2 until children.size) {
        val child = children[i]
        val value = child.value
        when (child.name) {
            "elif" -> {
                require (value is SymbolExpression && value.name == "pair")
                if (value.children[0].value.evalBoolean(parameterContext)) {
                    return value.children[1].value.eval(parameterContext)
                }
            }
            "else" -> {
                return value.eval(parameterContext)
            }
            else -> throw IllegalStateException("else or elif expected; got: '${child.name}'")
        }
    }
    return Unit
}

fun evalFor(children: List<ParameterReference>, parameterContext: LocalRuntimeContext): Any {
    val range = children[0].value.eval(parameterContext) as LongRange
    for (value in range) {
        (children[1].value as LambdaExpression).lambda.call(null, listOf(
            ParameterReference("", LiteralExpression(value))
        ), parameterContext)
    }
    return Unit
}
}