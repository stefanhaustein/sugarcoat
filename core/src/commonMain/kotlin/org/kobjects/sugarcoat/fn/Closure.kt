package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.LambdaExpression
import org.kobjects.sugarcoat.base.RuntimeContext

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
        receiver: RuntimeContext,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext = when (expression) {
        is LambdaExpression -> expression.lambda.call(receiver, children, context)
        else -> expression.eval(context)
    }
}