package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.base.TypeReference
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.ImplInstance
import org.kobjects.sugarcoat.model.Instance

class AsExpression(
    val context: Classifier,
    val source: Expression,
    target: Expression
): Expression {
    val target = TypeReference(context, (target as SymbolExpression).name, emptyList())

    override fun eval(context: LocalRuntimeContext): Instance {
        val def = context.globalRuntimeContext.findImpl(source.getType().resolve(), target.resolve())
        return ImplInstance(def, source.eval(context))
    }

    override fun getType() = target
}