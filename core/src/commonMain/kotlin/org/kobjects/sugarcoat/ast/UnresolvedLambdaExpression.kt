package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.Lambda
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.type.UnresolvedType

class UnresolvedLambdaExpression(
    position: Position,
    val parameters: List<Pair<String, Type?>>,
    val body: Expression
) : Expression(position) {
    override fun eval(context: LocalRuntimeContext) = throw UnsupportedOperationException()

    override fun getType(): Type = throw UnsupportedOperationException()

    override fun resolve(context: ResolutionContext, expectedType: Type?): Expression {
        if (expectedType !is FunctionType) {
            require(parameters.isEmpty()) {
                "${position}: No lambda parameters supported for expected type $expectedType"
            }
            return body.resolve(context, expectedType)
        }

        require (expectedType.parameterTypes.size == parameters.size) {
            "Expected ${expectedType.parameterTypes.size} parameters ${expectedType.parameterTypes} but got ${parameters.size} $parameters"
        }

        val innerContext = ResolutionContext(context.namespace, context)

        for ((i, expectedParameter) in expectedType.parameterTypes.withIndex()) {
            val lambdaParameter = parameters[i]
            val lambdaParameterType = lambdaParameter.second?.resolve(context.namespace)
            require(lambdaParameterType?.assignableFrom(expectedParameter.type) ?: true) {
                "Lambda parameter $lambdaParameter is not assignable from $expectedParameter"
            }
            innerContext.addLocal(lambdaParameter.first, lambdaParameterType ?: expectedParameter.type, false)
        }
        val resolvedBody = body.resolve(innerContext, expectedType.returnType)

        return LiteralExpression(Lambda(expectedType, parameters.map { it.first }, resolvedBody))

    }
}