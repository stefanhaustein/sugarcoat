package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class LambdaExpression(
    val lambda: FunctionDefinition
) : Expression {
    override fun eval(context: LocalRuntimeContext): Any {
        require (lambda.parameters.isEmpty()) { "Missing parameter value(s)." }
        return lambda.body.eval(context)
    }

    override fun getType() = lambda.returnType
}