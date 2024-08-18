package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.LocalContext
import org.kobjects.sugarcoat.runtime.RuntimeContext

data class FunctionDefinition(
    val parameters: List<ParameterDefinition>,
    val body: Expression
) : RuntimeContext, Callable {

    override fun call(
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        val localContext = LocalContext(parameterContext)
        for (i in parameters.indices) {
            localContext.symbols[parameters[i].name] =
                if (parameters[i].resolve) children[i].value.eval(parameterContext)
                else Closure(children[i].value, parameterContext)
        }
        return body.eval(localContext)
    }

    override fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        throw UnsupportedOperationException("'$name' not supported for functions")
    }

    override fun toString() =
        "(${parameters.joinToString (", ")}):\n  $body"
}