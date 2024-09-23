package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.base.Scope
import org.kobjects.sugarcoat.datatype.NativeInstance
import org.kobjects.sugarcoat.datatype.StringType

class LiteralExpression(value: Any) : Expression {
    val value: NativeInstance = Scope.of(value)

    override fun eval(ctx: Scope) = value

    override fun getType() = value.type

    override fun toString() =
        if (value is StringType.Instance) "\"" + value.value.replace("\"", "\"\"").replace("\n", "\\n") + "\""
        else value.toString()
}