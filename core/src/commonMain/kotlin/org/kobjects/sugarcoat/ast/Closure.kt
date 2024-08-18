package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RuntimeContext

data class Closure(
    val expression: Expression,
    val context: RuntimeContext
) : RuntimeContext, Callable {
    override fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        throw UnsupportedOperationException("'$name' not supported for closure")

    }

    override fun call(
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext = when (expression) {
        is LambdaExpression -> expression.lambda.call(children, context)
        else -> expression.eval(context)
    }
}