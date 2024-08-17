package org.kobjects.sugarcoat.function

import org.kobjects.sugarcoat.Evaluable
import org.kobjects.sugarcoat.ParameterReference
import org.kobjects.sugarcoat.RuntimeContext

data class LambdaDeclaration(
    val parameters: List<ParameterDeclaration>,
    val body: Evaluable
) : RuntimeContext, Evaluable {
    fun eval(children: List<ParameterReference>, callerContext: RuntimeContext): RuntimeContext {
        val localContext = LocalContext(callerContext)
        for (i in parameters.indices) {
            localContext.symbols[parameters[i].name] = children[i].value.eval(callerContext)
        }
        return body.eval(localContext)
    }

    override fun eval(context: RuntimeContext): RuntimeContext {
        require (parameters.isEmpty()) { "Missing parameter value(s)." }
        return body.eval(context)
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