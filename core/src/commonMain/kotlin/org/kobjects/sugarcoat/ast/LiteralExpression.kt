package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.Lambda
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.Position

class LiteralExpression(
    position: Position,
    val value: Any
) : ResolvedExpression(position) {

    override fun eval(context: LocalRuntimeContext) = value

    override fun getType() = Type.of(value)

    override fun toString() =
        if (value is String) "\"" + value.replace("\"", "\"\"").replace("\n", "\\n") + "\""
        else value.toString()


    override fun resolve(context: ResolutionContext, expectedType: Type?): Expression {
        if (expectedType == null) {
            return this
        }

        if (expectedType == F64Type && getType() == I64Type) {
            return LiteralExpression(position, (value as Long).toDouble())
        }

        val result = if (expectedType is FunctionType && getType() !is FunctionType) {
            require(expectedType.parameterTypes.isEmpty()) {
                "$position: Cannot imply lambda for function type with parameters: $expectedType"
            }
            LiteralExpression(position, Lambda(FunctionType(getType(), emptyList()), emptyList(), this))
        }  else this

        require(expectedType.assignableFrom(result.getType())) {
            "$position: Expected type $expectedType is not assignable from Literal expression type ${result.getType()} of expression $result"
        }

        return result
    }
}