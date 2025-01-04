package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.model.ImplDefinition
import org.kobjects.sugarcoat.model.ImplInstance
import org.kobjects.sugarcoat.model.TraitDefinition
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.Type

class AsExpression(
    position: Position,
    val source: Expression,
    val trait: TraitDefinition,
    private val implDefinition: ImplDefinition,
) : ResolvedExpression(position) {
    override fun eval(context: LocalRuntimeContext): Any {
        // This should be resolved at resolution time.

        return ImplInstance(implDefinition, source.eval(context))
    }

    override fun serialize(writer: CodeWriter) {
        writer.append(trait)
        writer.append("(")
        source.serialize(writer)
        writer.append(")")
    }

    override fun getType() = trait
}