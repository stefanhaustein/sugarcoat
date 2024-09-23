package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Scope
import org.kobjects.sugarcoat.model.Instance

class LocalContext(
    val parentContext: Scope
) : Scope {
    val symbols = mutableMapOf<String, Scope>()

    override fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: Scope): Scope =
        when (val resolved = symbols[name]) {
            null -> {
                println("$name not found in $symbols")
                parentContext.evalSymbol(name, children, parameterContext)
            }
            is Callable -> resolved.call(
                if (parentContext is Instance) parentContext else null, children, parameterContext)
            else -> resolved
    }

}