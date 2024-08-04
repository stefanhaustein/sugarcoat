package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.Evaluable
import org.kobjects.sugarcoat.RuntimeContext

data class Lambda(
    val parameters: List<DeclaredParameter>,
    val body: Evaluable
) : Evaluable {
    fun eval(children: List<Parameter>, callerContext: RuntimeContext): Any {
        val localContext = LocalContext(callerContext)
        for (i in parameters.indices) {
            localContext.symbols[parameters[i].name] = children[i].value.eval(callerContext)
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