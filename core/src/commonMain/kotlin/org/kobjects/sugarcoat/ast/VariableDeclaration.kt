package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.CodeWriter
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
        context.declare(name, initialValue.eval(context), position)
        return Unit
    }

    override fun serialize(writer: CodeWriter) {
        writer.append(name)
        writer.append(" = ")
        initialValue.serialize(writer)
    }

    override fun getType() = VoidType

    override fun resolve(
        context: ResolutionContext,
        expectedType: Type?
    ): VariableDeclaration {
        VoidType.match(expectedType) {
            "$position: Expected return type must be void for assignments."
        }
        val resolvedType = explicitType?.resolveType(context.namespace)
        val resolvedValue = initialValue.resolve(context, resolvedType)
        context.addLocal(name, resolvedValue.getType(), mutable)
        return copy(initialValue = resolvedValue, explicitType = resolvedType)
    }
}