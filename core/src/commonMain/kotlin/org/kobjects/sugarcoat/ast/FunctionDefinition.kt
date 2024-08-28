package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.LocalContext
import org.kobjects.sugarcoat.runtime.RuntimeContext

data class FunctionDefinition(
    val receiverType: Type,
    val parameters: List<ParameterDefinition>,
    val returnType: Type,
    val body: Expression
) : RuntimeContext, Callable {

    override fun call(
        receiver: RuntimeContext,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {

        val parameterConsumer = ParameterConsumer(children)

        val localContext = LocalContext(receiver)
        for (p in parameters) {
            localContext.symbols[p.name] = parameterConsumer.read(p, parameterContext)
        }
        parameterConsumer.done()

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