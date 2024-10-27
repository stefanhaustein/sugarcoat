package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.model.Classifier

data class FunctionDefinition(
    override val parent: Classifier,
    override val fallback: Classifier,
    override val static: Boolean,
    override val name: String,
    var parameters: List<ParameterDefinition>,
    var returnType: Type,
) : TypedCallable, Classifier(parent, name, fallback) {

    var body: Expression = LiteralExpression(0)


    override fun call(
        receiver: Any?,
        children: List<ParameterReference>,
        parameterScope: LocalRuntimeContext
    ): Any {
        require(static == (receiver == null)) {
            if (static) "Unexpected receiver for static method." else "Receiver expected for instance method."
        }

        val parameterConsumer = ParameterConsumer(children)

        val localContext = LocalRuntimeContext(parameterScope.globalRuntimeContext, receiver)
        for (p in parameters) {
            localContext.symbols[p.name] = parameterConsumer.read(parameterScope, p)
        }
        parameterConsumer.done()

        return body.eval(localContext)
    }

    override val type: FunctionType
        get() = FunctionType(returnType, parameters.map { it.type })

    override fun serialize(sb: StringBuilder) {
        sb.append("fn $name(${parameters.joinToString (", ")})\n  ")
        body.stringify(sb, 0)
        sb.append("\n")
    }

    override fun resolveTypes() {
        super.resolveTypes()
        parameters = parameters.map { it.resolve(this) }
        returnType = returnType.resolve(this)
    }

    override fun resolveExpressions() {
        super.resolveExpressions()
        body = body.resolve(null)
    }

    override fun toString() =
        "fn $name"
}