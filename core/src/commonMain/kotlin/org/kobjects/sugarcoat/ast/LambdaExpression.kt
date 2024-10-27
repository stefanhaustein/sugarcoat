package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class LambdaExpression(
    val lambda: FunctionDefinition
) : Expression {

    override fun eval(context: LocalRuntimeContext) =
        lambda.call(context.instance, emptyList(), context)


    override fun resolve(expectedType: Type?): LambdaExpression {
        require(expectedType == null || expectedType is FunctionType)
        return this
    }

    override fun getType() = lambda.type

}