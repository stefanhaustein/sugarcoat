package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.LiteralExpression
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

    override fun resolveTypes() {
        super.resolveTypes()
        parameters = parameters.map { it.resolve(this) }
        returnType = returnType.resolve(this)
    }

    /** This is called for lambdas instead of resolveExpressions()/resolveTypes */
    fun resolveSignature(expectedType: FunctionType) {
        require (expectedType.parameterTypes.size == parameters.size) {
            "Expected ${expectedType.parameterTypes.size} parameters ${expectedType.parameterTypes} but got ${parameters.size} $parameters"
        }

        val builder = mutableListOf<ParameterDefinition>()
        for ((i, expectedParameter) in expectedType.parameterTypes.withIndex()) {
            builder.add(ParameterDefinition(parameters[i].name, expectedParameter.type))
            if (expectedParameter.type is UnresolvedType) {
                throw IllegalStateException("Unresolved type for expected parameter $expectedParameter for $this")
            }
        }
        parameters = builder.toList()

        super.resolveTypes()
        super.resolveExpressions()

        returnType = expectedType.returnType
        body = body.resolve(returnType)
    }

    override fun resolveExpressions() {
        super.resolveExpressions()
        body = body.resolve(null)
    }

    override fun toString() =
        "fn $name"
}