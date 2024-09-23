package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.model.AbstractClassifierDefinition
import org.kobjects.sugarcoat.model.Instance

data class FunctionDefinition(
    override val parent: Namespace,
    val static: Boolean,
    override val name: String,
    val parameters: List<ParameterDefinition>,
    val returnType: Type,
    val body: Expression
) : Instance, Callable, Namespace {

    override fun call(
        receiver: Any?,
        children: List<ParameterReference>,
        parameterScope: RuntimeContext
    ): Any {

        require(static == (receiver == null)) {
            if (static) "Unexpected receiver for static method." else "Receiver expected for instance method."
        }

        val parameterConsumer = ParameterConsumer(children)

        val localContext = RuntimeContext(this, receiver)
        for (p in parameters) {
            localContext.symbols[p.name] = parameterConsumer.read(parameterScope, p)
        }
        parameterConsumer.done()

        return body.eval(localContext)
    }

    override val type: AbstractClassifierDefinition
        get() = TODO("Not yet implemented")

    override fun toString() =
        "(${parameters.joinToString (", ")})\n  $body"
}