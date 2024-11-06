package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.ast.ResolutionContext
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.type.UnresolvedType
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
        children: List<Expression?>,
        parameterScope: LocalRuntimeContext
    ): Any {
        require(static == (receiver == null)) {
            if (static) "Unexpected receiver for static method." else "Receiver expected for instance method."
        }

        val localContext = LocalRuntimeContext(parameterScope.globalRuntimeContext, receiver)
        for ((i, p) in parameters.withIndex()) {
            localContext.symbols[p.name] = children[i]!!.eval(localContext)
        }

        return body.eval(localContext)
    }

    override val type: FunctionType
        get() = FunctionType(returnType, parameters)

    override fun serialize(sb: StringBuilder) {
        sb.append("fn $name(${parameters.joinToString (", ")})\n  ")
        body.stringify(sb, 0)
        sb.append("\n")
    }

    override fun resolveSignatures() {
        parameters = parameters.map { it.resolve(this) }
        returnType = returnType.resolve(this)
    }

    private fun createResolutionContext(): ResolutionContext {
        val resolutionContext = ResolutionContext(this)
        if (!static) {
            resolutionContext.addLocal("self", parent.selfType(), false)
        }
        for (parameter in parameters) {
            resolutionContext.addLocal(parameter.name, parameter.type, false)
        }
        return resolutionContext
    }


    override fun resolveExpressions() {
        body = body.resolve(createResolutionContext(), null)
    }

    override fun toString() =
        "fn $name"
}