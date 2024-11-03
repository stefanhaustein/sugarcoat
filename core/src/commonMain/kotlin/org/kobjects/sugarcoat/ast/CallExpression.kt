package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.TypedCallable
import org.kobjects.sugarcoat.parser.Position

class CallExpression(
    position: Position,
    val receiver: Expression?,
    val fn: TypedCallable,
    val parameter: List<Expression?>
): ResolvedExpression(position) {
    override fun eval(context: LocalRuntimeContext): Any =
        fn.call(receiver?.eval(context), parameter, context)

    override fun getType() = fn.type.returnType

}