package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.TypedCallable

data class CallExpression(
    val receiver: Expression?,
    val fn: TypedCallable,
    val parameter: List<ParameterReference>
): ResolvedExpression() {
    override fun eval(context: LocalRuntimeContext): Any =
        fn.call(receiver?.eval(context), parameter, context)

    override fun getType() = fn.type.returnType

}