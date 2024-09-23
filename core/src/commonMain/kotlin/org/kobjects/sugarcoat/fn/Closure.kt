package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.LambdaExpression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.model.AbstractClassifierDefinition
import org.kobjects.sugarcoat.model.Instance

data class Closure(
    val expression: Expression,
    val context: RuntimeContext
) : Instance, Callable {
    override val type: AbstractClassifierDefinition
        get() = throw UnsupportedOperationException()

    override fun call(
        receiver: Any?,
        children: List<ParameterReference>,
        parameterScope: RuntimeContext
    ) = when (expression) {
        is LambdaExpression -> expression.lambda.call(receiver, children, context)
        else -> expression.eval(context)
    }
}