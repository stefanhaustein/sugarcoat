package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.model.ImplDefinition
import org.kobjects.sugarcoat.model.ImplInstance
import org.kobjects.sugarcoat.model.TraitDefinition
import org.kobjects.sugarcoat.parser.Position

class AsExpression(
    position: Position,
    val source: Expression,
    val implDefinition: ImplDefinition,
) : ResolvedExpression(position) {
    override fun eval(context: LocalRuntimeContext): Any {
        // This should be resolved at resolution time.

        return ImplInstance(implDefinition, source.eval(context))
    }

    override fun getType() = implDefinition.trait
}