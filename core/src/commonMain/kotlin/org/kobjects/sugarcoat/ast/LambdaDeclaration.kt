package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.LocalContext
import org.kobjects.sugarcoat.runtime.RuntimeContext

data class LambdaDeclaration(
    val parameters: List<ParameterDeclaration>,
    val body: Node
) : Callable, RuntimeContext {
    override fun eval(children: List<ParameterReference>, callerContext: RuntimeContext): RuntimeContext {
        val localContext = LocalContext(callerContext)
        for (i in parameters.indices) {
            if (parameters[i].resolve) {
                localContext.symbols[parameters[i].name] = children[i].value.eval(callerContext)
            } else {
                throw UnsupportedOperationException("NYI: Build a closure with callercontext")
            }
        }
        return body.eval(localContext)
    }

    override fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        throw UnsupportedOperationException("$name not supported for lambda")
    }

    override fun toString() =
        "(${parameters.joinToString (", ")}):\n  $body"
}