package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class LiteralExpression(val value: Any) : ResolvedExpression() {

    override fun eval(context: LocalRuntimeContext) = value

    override fun getType() = Type.of(value)

    override fun toString() =
        if (value is String) "\"" + value.replace("\"", "\"\"").replace("\n", "\\n") + "\""
        else value.toString()
}