package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RuntimeContext
import org.kobjects.sugarcoat.datatype.StringContext

class LiteralNode(value: Any) : Node {
    val value = RuntimeContext.of(value)

    override fun eval(ctx: RuntimeContext) = value

    override fun toString() =
        if (value is StringContext) "\"" + value.value.replace("\"", "\"\"").replace("\n", "\\n") + "\""
        else value.toString()
}