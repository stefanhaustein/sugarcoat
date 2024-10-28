package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

data class VariableDeclaration(
    val name: String,
    val mutable: Boolean,
    val value: Expression) : Expression {

    override fun eval(context: LocalRuntimeContext): Any {

        require (!context.symbols.containsKey(name)) {
            "Local variable '$name' already declared in this context."
        }
        context.symbols[name] = value.eval(context)
        return Unit
    }

    override fun getType() = VoidType

    override fun toString() = "$name = $value"

    override fun resolve(expectedType: Type?) = copy(value = value.resolve(null))
}