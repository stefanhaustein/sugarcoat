package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.type.MetaType
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.TraitDefinition

class UnresolvedAsExpression(
    val context: Classifier,
    val source: Expression,
    val target: Expression
): Expression {

    override fun eval(context: LocalRuntimeContext) = throw UnsupportedOperationException("Unresolved.")

    override fun getType() = throw UnsupportedOperationException("Unresolved.")

    override fun resolve(context: ResolutionContext, expectedType: Type?): Expression {
        val resolvedTarget = target.resolve(ResolutionContext(), null)

        val type = resolvedTarget.getType()
        require (type is MetaType && type.type is TraitDefinition) {
            "Target must be Trait"
        }
        return AsExpression(source.resolve(ResolutionContext(), null), type.type)
    }
}