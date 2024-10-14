package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.LambdaExpression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.Instance

data class Closure(
    val expression: Expression,
    val context: LocalRuntimeContext
) : Instance, Callable {
    override val type: Classifier
        get() = throw UnsupportedOperationException()

    override fun call(
        receiver: Any?,
        children: List<ParameterReference>,
        parameterScope: LocalRuntimeContext
    ) = when (expression) {
        is LambdaExpression -> expression.lambda.call(receiver, children, context)
        else -> expression.eval(context)
    }
}