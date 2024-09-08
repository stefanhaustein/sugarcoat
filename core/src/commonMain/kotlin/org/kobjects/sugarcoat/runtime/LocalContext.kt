package org.kobjects.sugarcoat.runtime

import org.kobjects.sugarcoat.ast.Callable
import org.kobjects.sugarcoat.ast.FunctionDefinition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.datatype.VoidType

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