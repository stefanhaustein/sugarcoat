package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.BoolType
import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.runtime.RuntimeContext

interface Expression {
    fun eval(context: RuntimeContext): RuntimeContext

    fun evalDouble(context: RuntimeContext) = (eval(context) as F64Type.Instance).value
    fun evalBoolean(context: RuntimeContext) = (eval(context) as BoolType.Instance).value
    fun evalLong(context: RuntimeContext) = (eval(context) as I64Type.Instance).value

    fun stringify(stringBuilder: StringBuilder, parentPrecedence: Int) {
        stringBuilder.append(this)
    }
}