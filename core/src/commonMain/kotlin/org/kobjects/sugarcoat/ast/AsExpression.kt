package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.base.Scope
import org.kobjects.sugarcoat.base.TypeReference
import org.kobjects.sugarcoat.model.ImplInstance
import org.kobjects.sugarcoat.model.Instance

class AsExpression(
    val context: Namespace,
    val source: Expression,
    target: Expression
): Expression {
    val target = TypeReference(context, (target as SymbolExpression).name, emptyList())

    override fun eval(context: Scope): Instance {
        val def = this.context.findImpl(source.getType().resolve(), target.resolve())
        return ImplInstance(def, source.eval(context))
    }

    override fun getType() = target
}