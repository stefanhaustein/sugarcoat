package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.Lambda
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.Position

abstract class Expression(open val position: Position) {

    abstract fun eval(context: LocalRuntimeContext): Any

    open fun evalDouble(context: LocalRuntimeContext) = eval(context) as Double
    open fun evalBoolean(context: LocalRuntimeContext) = eval(context) as Boolean
    open fun evalLong(context: LocalRuntimeContext) = eval(context) as Long

    open fun serialize(writer: CodeWriter) {
        writer.append(this)
    }

    /** Note that genericTypeResolve is not rolled into context, as it has a different (shorter) scope */
    abstract fun resolve(
        context: ResolutionContext,
        expectedType: Type?
    ): Expression

    abstract fun getType(): Type

    fun asLambda(functionType: FunctionType) =
        LiteralExpression(
            position,
            Lambda(functionType, true, emptyList(), this)
        )




}