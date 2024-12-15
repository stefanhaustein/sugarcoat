package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ResolutionContext
import org.kobjects.sugarcoat.ast.UnresolvedFunctionBody
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.parser.Position

data class FunctionDefinition(
    val position: Position,
    override val parent: Classifier,
    override val fallback: Classifier,
    override val static: Boolean,
    override val name: String,
    override var type: FunctionType,
) : Callable, Classifier(parent, name, fallback) {

    var body: Expression = UnresolvedFunctionBody(this)

    override fun call(
        receiver: Any?,
        children: List<Expression?>,
        parameterScope: LocalRuntimeContext
    ): Any {
        require(static == (receiver == null)) {
            if (static) "Unexpected receiver for static method." else "Receiver expected for instance method."
        }

        val localContext = LocalRuntimeContext(parameterScope.globalRuntimeContext, receiver)
        for ((i, p) in type.parameterTypes.withIndex()) {
            localContext.declare(p.name, children[i]?.eval(parameterScope) ?: p.defaultValue?.eval(parameterScope) ?: throw IllegalStateException("Parameter not foud: ${p.name} in $localContext"))
        }

        return body.eval(localContext)
    }

    override fun serialize(writer: CodeWriter) {
        writer.append("fn $name(${type.parameterTypes.joinToString (", ")})")
        writer.indent()
        writer.newline()
        body.serialize(writer)
        writer.outdent()
        writer.newline()
    }

    override fun resolveSignatures() {
        type = type.resolveType(parent)
    }

    private fun createResolutionContext(): ResolutionContext {
        val resolutionContext = ResolutionContext(this)
        if (!static) {
            resolutionContext.addLocal("self", parent.selfType(), false)
        }
        for (parameter in type.parameterTypes) {
            resolutionContext.addLocal(parameter.name, parameter.type, false)
        }
        return resolutionContext
    }


    override fun resolveExpressions() {
        type = type.resolveDefaultExpressions(ResolutionContext(parent))
        // TODO: Hand in function return type to type resolution.
        body = body.resolve(createResolutionContext(), null/*type.returnType*/)
    }

    override fun toString() =
        "fn $name"
}