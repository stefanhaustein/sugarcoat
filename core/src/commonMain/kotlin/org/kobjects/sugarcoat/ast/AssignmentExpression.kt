package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.base.RuntimeContext

class AssignmentExpression(val target: Expression, val value: Expression): Expression {
    override fun eval(context: RuntimeContext): RuntimeContext {
        TODO("Not yet implemented")
    }
}