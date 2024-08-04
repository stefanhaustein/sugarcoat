package org.kobjects.sugarcoat

class IfContext(
    val resolved: Any?
) : RuntimeContext {
    override fun evalSymbol(
        name: String,
        children: List<Parameter>,
        parameterContext: RuntimeContext
    ) = throw UnsupportedOperationException()

    override fun evalMethod(
        name: String,
        children: List<Parameter>,
        parameterContext: RuntimeContext
    ): Any =when(name) {
        "elif" -> if (resolved != null || !children[1].value.evalBoolean(parameterContext)) this else IfContext(children[2].value.eval(parameterContext))
        "else" -> resolved ?: children[1].value.eval(parameterContext)
        else -> throw UnsupportedOperationException("Unrecognized method $name for IfContext")
    }
}