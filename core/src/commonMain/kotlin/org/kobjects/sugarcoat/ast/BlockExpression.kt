package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.Lambda
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.Type

data class BlockExpression(
    override val position: Position,
    val sequence: List<Expression>
) : Expression(position) {
    override fun eval(context: LocalRuntimeContext): Any {
        var result: Any = Unit
        for (expr in sequence) {
            result = expr.eval(context)
        }
        return result
    }

    override fun resolve(context: ResolutionContext, expectedType: Type?): Expression {
        if (sequence.isEmpty()) {
            return BlockExpression(position, emptyList())
        }
        val result = MutableList(sequence.size - 1) {
            sequence[it].resolve(context, VoidType)
        }
        val lastResolved = sequence.last().resolve(context, expectedType)
        if (lastResolved is LiteralExpression && lastResolved.value is Lambda && lastResolved.value.implied) {
            result.add(lastResolved.value.body)
            return BlockExpression(position, result).asLambda(expectedType as FunctionType)
        }
        result.add(lastResolved)
        return BlockExpression(position, result)
    }

    override fun getType(): Type {
        return sequence.lastOrNull()?.getType() ?: VoidType
    }

    override fun serialize(writer: CodeWriter) {
        for ((index, expr) in sequence.withIndex()) {
            if (index > 0) {
                writer.newline()
            }
            expr.serialize(writer)
        }
    }
}