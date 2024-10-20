package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.base.Typed
import org.kobjects.sugarcoat.model.Classifier

data class FunctionDefinition(
    override val parent: Classifier,
    override val fallback: Classifier,
    override val static: Boolean,
    override val name: String,
    val parameters: List<ParameterDefinition>,
    val returnType: Type,
    val body: Expression
) : Callable, Classifier(parent, name, fallback), Typed {

    override fun call(
        receiver: Any?,
        children: List<ParameterReference>,
        parameterScope: LocalRuntimeContext
    ): Any {

        require(static == (receiver == null)) {
            if (static) "Unexpected receiver for static method." else "Receiver expected for instance method."
        }

        val parameterConsumer = ParameterConsumer(children)

        val localContext = LocalRuntimeContext(parameterScope.globalRuntimeContext, this, receiver)
        for (p in parameters) {
            localContext.symbols[p.name] = parameterConsumer.read(parameterScope, p)
        }
        parameterConsumer.done()

        return body.eval(localContext)
    }

    override val type: Type
        get() = FunctionType(parameters.map { it.type }, returnType)

    override fun serialize(sb: StringBuilder) {
        sb.append("fn $name(${parameters.joinToString (", ")})\n  ")
        body.stringify(sb, 0)
        sb.append("\n")
    }

    override fun toString() =
        "fn $name"
}