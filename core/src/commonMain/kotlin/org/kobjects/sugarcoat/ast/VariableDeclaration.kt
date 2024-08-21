package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.VoidContext
import org.kobjects.sugarcoat.runtime.LocalContext
import org.kobjects.sugarcoat.runtime.RuntimeContext

class VariableDeclaration(
    val name: String,
    val value: Expression) : Expression {

    override fun eval(context: RuntimeContext): RuntimeContext {
        require(context is LocalContext) {
            "Local context required"
        }
        require (!context.symbols.containsKey(name)) {
            "Local variable '$name' already declared in this context."
        }
        context.symbols["name"] = value.eval(context)
        return VoidContext
    }

    override fun toString() = "$name = $value"
}