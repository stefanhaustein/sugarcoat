package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.base.Definition
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.base.RuntimeContext
import org.kobjects.sugarcoat.model.Instance

data class FunctionDefinition(
    override val parent: Definition,
    val static: Boolean,
    override val name: String,
    val parameters: List<ParameterDefinition>,
    val returnType: Type,
    val body: Expression
) : RuntimeContext, Callable, Definition {

    override fun call(
        receiver: Instance?,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {

        require(static == (receiver == null)) {
            if (static) "Unexpected receiver for static method." else "Receiver expected for instance method."
        }

        val parameterConsumer = ParameterConsumer(children)

        val localContext = LocalContext(receiver ?: parameterContext)
        for (p in parameters) {
            localContext.symbols[p.name] = parameterConsumer.read(parameterContext, p)
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
        "(${parameters.joinToString (", ")})\n  $body"
}