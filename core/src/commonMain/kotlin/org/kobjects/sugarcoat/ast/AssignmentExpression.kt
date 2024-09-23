package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.RuntimeContext
import org.kobjects.sugarcoat.model.Instance

class AssignmentExpression(val target: Expression, val value: Expression): Expression {
    override fun eval(context: RuntimeContext): Instance {
        TODO("Not yet implemented")
    }

    override fun getType() = VoidType


}