package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RuntimeContext
import org.kobjects.sugarcoat.datatype.StringType

class LiteralExpression(value: Any) : Expression {
    val value = RuntimeContext.of(value)

    override fun eval(ctx: RuntimeContext) = value

    override fun toString() =
        if (value is StringType.Instance) "\"" + value.value.replace("\"", "\"\"").replace("\n", "\\n") + "\""
        else value.toString()
}