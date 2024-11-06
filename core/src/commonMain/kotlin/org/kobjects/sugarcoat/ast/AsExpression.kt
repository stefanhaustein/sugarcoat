package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.model.ImplInstance
import org.kobjects.sugarcoat.model.TraitDefinition
import org.kobjects.sugarcoat.parser.Position

class AsExpression(
    position: Position,
    val source: Expression,
    val target: TraitDefinition
) : ResolvedExpression(position) {
    override fun eval(context: LocalRuntimeContext): Any {
        // This should be resolved at resolution time.
        val implDefinition = context.globalRuntimeContext.program.findImpl(source.getType(), target)
        return ImplInstance(implDefinition, source.eval(context))
    }

    override fun getType() = target
}