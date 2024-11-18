package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.Type

data class ParenthesizedExpression(
    override val position: Position,
    val child: Expression) : Expression(position) {
    override fun eval(context: LocalRuntimeContext) = child.eval(context)

    override fun resolve(context: ResolutionContext, expectedType: Type?) =
        ParenthesizedExpression(position, child.resolve(context, expectedType))

    override fun getType(): Type = child.getType()

    override fun stringify(stringBuilder: StringBuilder) {
        stringBuilder.append('(')
        child.stringify(stringBuilder)
        stringBuilder.append(')')
    }
}