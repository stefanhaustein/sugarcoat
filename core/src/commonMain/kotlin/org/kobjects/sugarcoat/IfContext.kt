package org.kobjects.sugarcoat

class IfContext(
    val resolved: Any?
) : RuntimeContext {
    override fun evalSymbol(
        name: String,
        children: List<Parameter>,
        parameterContext: RuntimeContext
    ): Any = when(name) {
        "elif" -> if (resolved != null || !children.first().value.evalBoolean(parameterContext)) this else IfContext(children[1].value.eval(parameterContext))
        "else" -> resolved ?: children.first().value.eval(parameterContext)
        else -> throw UnsupportedOperationException("Unrecognized method $name for IfContext")
    }
}