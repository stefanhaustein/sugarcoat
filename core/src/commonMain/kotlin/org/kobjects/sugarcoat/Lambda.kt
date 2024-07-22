package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.Evaluable
import org.kobjects.sugarcoat.RuntimeContext

data class Lambda(
    val parameters: List<String>,
    val body: Evaluable
) : Evaluable {
    fun eval(children: List<Parameter>, callerContext: RuntimeContext): Any {
        val localContext = LocalContext(callerContext)
        for (i in parameters.indices) {
            localContext.symbols[parameters[i]] = children[i].value.eval(callerContext)
        }
        return body.eval(localContext)
    }

    override fun eval(context: RuntimeContext): Any {
        require (parameters.isEmpty()) { "Missing parameter value(s)." }
        return body.eval(context)
    }

    override fun toString() =
        "(${parameters.joinToString (", ")}):\n  $body"
}