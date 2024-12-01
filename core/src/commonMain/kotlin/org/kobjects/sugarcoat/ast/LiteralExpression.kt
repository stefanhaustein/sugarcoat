package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.GenericTypeResolver

class LiteralExpression(
    position: Position,
    val value: Any
) : ResolvedExpression(position) {

    override fun eval(context: LocalRuntimeContext) = value

    override fun getType() = Type.of(value)

    override fun toString() =
        if (value is String) "\"" + value.replace("\"", "\"\"").replace("\n", "\\n") + "\""
        else value.toString()


    override fun resolve(
        context: ResolutionContext,
        genericTypeResolver: GenericTypeResolver,
        expectedType: Type?
    ): Expression {
        if (expectedType == null) {
            return this
        }

        if (expectedType == F64Type && getType() == I64Type) {
            return LiteralExpression(position, (value as Long).toDouble())
        }

       return context.resolveTypeExpectation(this, genericTypeResolver, getType(), expectedType)
    }
}