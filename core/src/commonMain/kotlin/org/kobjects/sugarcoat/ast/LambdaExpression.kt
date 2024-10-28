package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class LambdaExpression(
    val lambda: FunctionDefinition
) : Expression {

    override fun eval(context: LocalRuntimeContext) = lambda

    override fun resolve(expectedType: Type?): Expression {
        if (expectedType == null) {
            return this
        }

        // "Un-lambda"
        if (expectedType !is FunctionType) {
            require(lambda.type.parameterTypes.isEmpty()) {
               "Can't inline a lambda function that requires parameters"
            }
            return lambda.body
        }

        // Function type required, let's resolve the lambda

        lambda.resolveSignature(expectedType)

        return this
    }

    override fun getType() = lambda.type

}