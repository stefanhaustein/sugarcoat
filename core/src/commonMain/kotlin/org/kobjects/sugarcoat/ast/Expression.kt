package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

interface Expression {
    fun eval(context: LocalRuntimeContext): Any

    fun evalDouble(context: LocalRuntimeContext) = eval(context) as Double
    fun evalBoolean(context: LocalRuntimeContext) = eval(context) as Boolean
    fun evalLong(context: LocalRuntimeContext) = eval(context) as Long

    fun stringify(stringBuilder: StringBuilder, parentPrecedence: Int) {
        stringBuilder.append(this)
    }

    fun getType(): Type
}