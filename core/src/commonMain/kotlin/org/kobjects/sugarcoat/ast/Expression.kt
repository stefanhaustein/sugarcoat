package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.GenericTypeResolver

abstract class Expression(open val position: Position) {

    abstract fun eval(context: LocalRuntimeContext): Any

    open fun evalDouble(context: LocalRuntimeContext) = eval(context) as Double
    open fun evalBoolean(context: LocalRuntimeContext) = eval(context) as Boolean
    open fun evalLong(context: LocalRuntimeContext) = eval(context) as Long

    open fun stringify(stringBuilder: StringBuilder) {
        stringBuilder.append(this)
    }

    abstract fun resolve(
        context: ResolutionContext,
        genericTypeResolver: GenericTypeResolver,
        expectedType: Type?
    ): Expression

    abstract fun getType(): Type





}