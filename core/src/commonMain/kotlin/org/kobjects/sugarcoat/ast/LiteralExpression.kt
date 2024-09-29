package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class LiteralExpression(val value: Any) : Expression {

    override fun eval(context: LocalRuntimeContext) = value

    override fun getType() = Type.of(value)

    override fun toString() =
        if (value is String) "\"" + value.replace("\"", "\"\"").replace("\n", "\\n") + "\""
        else value.toString()
}