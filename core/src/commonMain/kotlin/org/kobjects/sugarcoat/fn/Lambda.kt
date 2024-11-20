package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression

class Lambda(
    override val type: FunctionType,
    val parameterNames: List<String>,
    val body: Expression
) : Callable {

    override val static: Boolean
        get() = true

    override fun call(
        receiver: Any?,
        children: List<Expression?>,
        parameterScope: LocalRuntimeContext
    ): Any {
        require(static == (receiver == null)) {
            if (static) "Unexpected receiver for static method." else "Receiver expected for instance method."
        }

        val localContext = LocalRuntimeContext(parameterScope.globalRuntimeContext, receiver)
        for ((i, p) in parameterNames.withIndex()) {
            localContext.symbols[p] = children[i]!!.eval(localContext)
        }

        return body.eval(localContext)
    }
}