package org.kobjects.sugarcoat.runtime

import org.kobjects.sugarcoat.ast.LambdaDeclaration
import org.kobjects.sugarcoat.ast.ParameterReference

class LocalContext(
    val parentContext: RuntimeContext
) : RuntimeContext {
    val symbols = mutableMapOf<String, RuntimeContext>()

    override fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext =
        when (val resolved = symbols[name]) {
            null -> parentContext.evalSymbol(name, children, parameterContext)
            is LambdaDeclaration -> resolved.eval(children, parameterContext)
            else -> resolved
    }

}