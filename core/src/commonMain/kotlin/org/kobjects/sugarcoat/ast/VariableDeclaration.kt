package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.Position

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
        expectedType: Type?
    ): VariableDeclaration {
        require(expectedType == null || expectedType.assignableFrom(VoidType)) {
            "Expected return type must be void for assignments."
        }
        val resolvedValue = initialValue.resolve(context, explicitType)
        context.addLocal(name, resolvedValue.getType(), mutable)
        return copy(initialValue = resolvedValue)
    }
}