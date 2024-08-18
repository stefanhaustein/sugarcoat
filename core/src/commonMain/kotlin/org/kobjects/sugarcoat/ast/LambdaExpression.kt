package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RuntimeContext

class LambdaExpression(
    val lambda: FunctionDefinition
) : Expression {
    override fun eval(context: RuntimeContext): RuntimeContext {
        require (lambda.parameters.isEmpty()) { "Missing parameter value(s)." }
        return lambda.body.eval(context)
    }
}