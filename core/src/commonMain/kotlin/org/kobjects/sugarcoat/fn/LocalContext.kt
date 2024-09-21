package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.RuntimeContext

class LocalContext(
    val parentContext: RuntimeContext
) : RuntimeContext {
    val symbols = mutableMapOf<String, RuntimeContext>()

    override fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext =
        when (val resolved = symbols[name]) {
            null -> {
                println("$name not found in $symbols")
                parentContext.evalSymbol(name, children, parameterContext)
            }
            is Callable -> resolved.call(parameterContext, children, parameterContext)
            else -> resolved
    }

}