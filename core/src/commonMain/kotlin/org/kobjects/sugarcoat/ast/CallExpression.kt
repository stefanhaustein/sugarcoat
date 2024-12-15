package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.Lambda
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.parser.Position

data class CallExpression(
    override val position: Position,
    val receiver: Expression?,
    val fn: Callable,
    val parameter: List<Expression?>
): ResolvedExpression(position) {
    override fun eval(context: LocalRuntimeContext): Any {
        try {
            return fn.call(receiver?.eval(context), parameter, context)
        } catch (e: Exception) {
            if (e.message?.contains(": Native call exception in") ?: false) {
                throw e
            } else {
                throw RuntimeException("$position: Native call exception in $fn", e)
            }
        }
    }

    override fun getType() = fn.type.returnType


    override fun serialize(writer: CodeWriter) {
        writer.writeInvocation(
            receiver,
            if (fn is Classifier) fn.name else fn.toString(),
            parameter.mapIndexed { index, experession ->
                fn.type.parameterTypes[index].name to experession
            }
        )
    }

}