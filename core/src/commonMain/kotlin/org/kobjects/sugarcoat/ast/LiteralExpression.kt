package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64Type
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


    override fun serialize(writer: CodeWriter) {
        when(value) {
            is String -> writer.append("\"" + value.replace("\"", "\"\"").replace("\n", "\\n") + "\"")
            is Lambda -> {
                writer.append(":: ")
                writer.append(value.parameterNames.joinToString())
                writer.append("  # ${value.type}")
                writer.indent()
                writer.newline()
                value.body.serialize(writer)
                writer.outdent()
            }
            else -> writer.append(value)
        }
    }

    override fun resolve(
        context: ResolutionContext,
        expectedType: Type?
    ): Expression {
        if (expectedType == null) {
            return this
        }

        if (expectedType == F64Type && getType() == I64Type) {
            return LiteralExpression(position, (value as Long).toDouble())
        }

       return context.resolveTypeExpectation(this, getType(), expectedType)
    }
}