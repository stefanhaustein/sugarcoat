package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RuntimeContext
import org.kobjects.sugarcoat.datatype.BooleanContext
import org.kobjects.sugarcoat.datatype.DoubleContext
import org.kobjects.sugarcoat.datatype.LongContext

interface Expression {
    fun eval(context: RuntimeContext): RuntimeContext

    fun evalDouble(context: RuntimeContext) = (eval(context) as DoubleContext).value
    fun evalBoolean(context: RuntimeContext) = (eval(context) as BooleanContext).value
    fun evalLong(context: RuntimeContext) = (eval(context) as LongContext).value

    fun stringify(stringBuilder: StringBuilder, parentPrecedence: Int) {
        stringBuilder.append(this)
    }
}