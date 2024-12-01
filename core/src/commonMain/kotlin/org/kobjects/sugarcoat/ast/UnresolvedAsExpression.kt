package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.type.MetaType
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.model.TraitDefinition
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.GenericTypeResolver

class UnresolvedAsExpression(
    position: Position,
    val source: Expression,
    val target: Expression
): Expression(position) {

    override fun eval(context: LocalRuntimeContext) = throw UnsupportedOperationException("Unresolved.")

    override fun getType() = throw UnsupportedOperationException("Unresolved.")

    override fun resolve(
        context: ResolutionContext,
        genericTypeResolver: GenericTypeResolver,
        expectedType: Type?
    ): Expression {
        val resolvedTarget = target.resolve(context, genericTypeResolver, null)
        val resolvedSource = source.resolve(context, genericTypeResolver, null)

        val type = resolvedTarget.getType()
        require (type is MetaType && type.type is TraitDefinition) {
            "$position: Target must be Trait; got $type"
        }
        val implDefinition = context.namespace.program.findImpl(resolvedSource.getType(), type.type)

        return AsExpression(position, resolvedSource, implDefinition)
    }
}