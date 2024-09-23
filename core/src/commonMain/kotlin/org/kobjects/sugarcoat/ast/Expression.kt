package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.BoolType
import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.fn.RuntimeContext

interface Expression {
    fun eval(context: RuntimeContext): Any

    fun evalDouble(context: RuntimeContext) = eval(context) as Double
    fun evalBoolean(context: RuntimeContext) = eval(context) as Boolean
    fun evalLong(context: RuntimeContext) = eval(context) as Long

    fun stringify(stringBuilder: StringBuilder, parentPrecedence: Int) {
        stringBuilder.append(this)
    }

    fun getType(): Type
}