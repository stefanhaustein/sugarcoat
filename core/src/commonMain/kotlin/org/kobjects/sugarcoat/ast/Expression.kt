package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.BoolType
import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.base.Scope
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.model.Instance

interface Expression {
    fun eval(context: Scope): Scope

    fun evalDouble(context: Scope) = (eval(context) as F64Type.Instance).value
    fun evalBoolean(context: Scope) = (eval(context) as BoolType.Instance).value
    fun evalLong(context: Scope) = (eval(context) as I64Type.Instance).value

    fun stringify(stringBuilder: StringBuilder, parentPrecedence: Int) {
        stringBuilder.append(this)
    }

    fun getType(): Type
}