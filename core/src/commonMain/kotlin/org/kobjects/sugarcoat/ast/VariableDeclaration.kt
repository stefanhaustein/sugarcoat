package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.LocalContext
import org.kobjects.sugarcoat.base.Scope
import org.kobjects.sugarcoat.model.Instance

class VariableDeclaration(
    val name: String,
    val mutable: Boolean,
    val value: Expression) : Expression {

    override fun eval(context: Scope): Instance {
        require(context is LocalContext) {
            "Local context required"
        }
        require (!context.symbols.containsKey(name)) {
            "Local variable '$name' already declared in this context."
        }
        context.symbols[name] = value.eval(context)
        return VoidType.VoidInstance
    }

    override fun getType() = VoidType

    override fun toString() = "$name = $value"
}