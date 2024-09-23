package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.base.Scope
import org.kobjects.sugarcoat.model.Instance

class LambdaExpression(
    val lambda: FunctionDefinition
) : Expression {
    override fun eval(context: Scope): Scope {
        require (lambda.parameters.isEmpty()) { "Missing parameter value(s)." }
        return lambda.body.eval(context)
    }

    override fun getType() = lambda.returnType
}