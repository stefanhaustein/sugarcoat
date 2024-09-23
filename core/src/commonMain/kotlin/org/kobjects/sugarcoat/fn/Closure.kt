package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.LambdaExpression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Scope
import org.kobjects.sugarcoat.model.AbstractClassifierDefinition
import org.kobjects.sugarcoat.model.Instance

data class Closure(
    val expression: Expression,
    val context: Scope
) : Instance, Callable {
    override val type: AbstractClassifierDefinition
        get() = throw UnsupportedOperationException()

    override fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: Scope
    ): Instance {
        throw UnsupportedOperationException("'$name' not supported for closure")

    }

    override fun call(
        receiver: Instance?,
        children: List<ParameterReference>,
        parameterScope: Scope
    ) = when (expression) {
        is LambdaExpression -> expression.lambda.call(receiver, children, context)
        else -> expression.eval(context)
    }
}