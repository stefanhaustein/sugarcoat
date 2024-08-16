package org.kobjects.sugarcoat

class Literal(value: Any) : Evaluable {
    val value = RuntimeContext.of(value)

    override fun eval(ctx: RuntimeContext) = value

    override fun toString() =
        if (value is StringContext) "\"" + value.value.replace("\"", "\"\"").replace("\n", "\\n") + "\""
        else value.toString()
}