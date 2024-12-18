package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.Lambda
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.model.TraitDefinition
import org.kobjects.sugarcoat.parser.Position

abstract class Expression(open val position: Position) {

    abstract fun eval(context: LocalRuntimeContext): Any

    open fun evalDouble(context: LocalRuntimeContext) = eval(context) as Double
    open fun evalBoolean(context: LocalRuntimeContext) = eval(context) as Boolean
    open fun evalLong(context: LocalRuntimeContext) = eval(context) as Long

    abstract fun serialize(writer: CodeWriter)

    final override fun toString() = CodeWriter().apply { serialize(this) }.toString()

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

    fun withImpliedTransformations(
        context: ResolutionContext,
        expectedType: Type?,
        actualType: Type,
        resolve: (Type?) -> Expression
    ): Expression {
        if (expectedType is FunctionType && expectedType.parameterTypes.isEmpty() && actualType !is FunctionType) {
            val result = withImpliedTransformations(context, expectedType.returnType, actualType, resolve)
            return result.asLambda(expectedType)
        }
        if (expectedType is TraitDefinition && actualType != expectedType) {
            val impl = context.namespace.program.findImpl(actualType, expectedType)
            val result = resolve(actualType)
            return AsExpression(position, result, impl)
        }
        return resolve(expectedType)
    }



}