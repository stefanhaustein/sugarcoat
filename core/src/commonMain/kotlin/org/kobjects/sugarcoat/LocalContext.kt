package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.Evaluable
import org.kobjects.sugarcoat.RuntimeContext

class LocalContext(
    val parentContext: RuntimeContext
) : RuntimeContext {
    val symbols = mutableMapOf<String, Any>()

    override fun evalSymbol(name: String, children: List<Parameter>, parameterContext: RuntimeContext): Any =
        when (val resolved = symbols[name]) {
            null -> parentContext.evalSymbol(name, children, parameterContext)
            is Lambda -> resolved.eval(children, parameterContext)
            else -> resolved
    }

}