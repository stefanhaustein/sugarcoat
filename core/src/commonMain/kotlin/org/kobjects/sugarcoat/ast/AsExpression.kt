package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RuntimeContext

class AsExpression(
    val source: Expression,
    val target: Expression
): Expression {
    override fun eval(context: RuntimeContext): RuntimeContext {
        TODO("Not yet implemented")
    }
}