package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.GenericTypeResolver

data class VariableDeclaration(
    override val position: Position,
    val name: String,
    val mutable: Boolean,
    val explicitType: Type?,
    val initialValue: Expression) : Expression(position) {

    override fun eval(context: LocalRuntimeContext): Any {

        require (!context.symbols.containsKey(name)) {
            "Local variable '$name' already declared in this context."
        }
        context.symbols[name] = initialValue.eval(context)
        return Unit
    }

    override fun getType() = VoidType

    override fun toString() = "$name = $initialValue"

    override fun resolve(
        context: ResolutionContext,
        genericTypeResolver: GenericTypeResolver,
        expectedType: Type?
    ): VariableDeclaration {
        val resolvedType = expectedType?.resolveType(context.namespace)
        resolvedType?.match(VoidType, genericTypeResolver) {
            "$position: Expected return type must be void for assignments."
        }
        val resolvedValue = initialValue.resolve(context, genericTypeResolver, resolvedType)
        context.addLocal(name, resolvedValue.getType(), mutable)
        return copy(initialValue = resolvedValue, explicitType = resolvedType)
    }
}