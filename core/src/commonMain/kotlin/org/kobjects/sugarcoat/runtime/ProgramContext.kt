package org.kobjects.sugarcoat.runtime

import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.Program
import org.kobjects.sugarcoat.ast.SymbolExpression
import org.kobjects.sugarcoat.datatype.RangeContext
import org.kobjects.sugarcoat.datatype.VoidContext
import org.kobjects.sugarcoat.ast.LambdaExpression

class ProgramContext(
    val program: Program,
    val printFn: (String) -> Unit = ::print
) : RuntimeContext {

    override fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext =
        program.functions[name]?.call(children, parameterContext) ?: when (name) {
            "for" -> evalFor(children, parameterContext)
            "if" -> evalIf(children, parameterContext)
            "print" -> {
                printFn(children.joinToString { it.value.eval(parameterContext).toString() })
                VoidContext
            }
            "range" -> when (children.size) {
                1 -> RangeContext(LongRange(0, children[0].value.evalLong(parameterContext) - 1))
                2 -> RangeContext(LongRange(
                    children.first().value.evalLong(parameterContext),
                    children.last().value.evalLong(parameterContext) - 1
                ))
                else -> throw IllegalArgumentException("2 or 3 parameter expected for range, but got ${children.size}")
            }

            "seq" -> children.fold<ParameterReference, RuntimeContext>(VoidContext) { _, current -> current.value.eval(parameterContext) }
            "=" -> {
                require(children.size == 2) { "Two parameters expected for assignment"}
                val target = (children.first() as LiteralExpression).value as String
                (parameterContext as LocalContext).symbols[target] = children.last().value.eval(parameterContext)
                VoidContext
            }
            "while" -> {
                require(children.size == 2) { "Two parameters expected for 'while'."}
                while (children[0].value.evalBoolean(parameterContext)) children[1].value.eval(parameterContext)
                VoidContext
            }
            else -> throw IllegalStateException("Unrecognized symbol: $name")
        }

    fun evalIf(children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext {
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
        return VoidContext
    }

    fun evalFor(children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext {
        val range = (children[0].value.eval(parameterContext) as RangeContext).value
        for (value in range) {
            (children[1].value as LambdaExpression).lambda.call(listOf(ParameterReference("", LiteralExpression(value))), parameterContext)
        }
        return VoidContext
    }
}