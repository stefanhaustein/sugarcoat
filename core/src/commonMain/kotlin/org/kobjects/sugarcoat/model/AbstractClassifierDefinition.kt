package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.RootContext
import org.kobjects.sugarcoat.base.Scope
import org.kobjects.sugarcoat.base.Type

abstract class AbstractClassifierDefinition(
    override val parent: Namespace?,
    override val name: String
): Namespace, Scope, Type {
    val definitions = mutableMapOf<String, Namespace>()

    override fun addDefinition(value: Namespace) {
        if (value.name.isEmpty()) {
            parent!!.addDefinition(value)
        } else {
            require(!definitions.contains(value.name)) { "Symbol defined already: '${value.name}'" }
            definitions[value.name] = value
        }
    }

    override fun get(name: String): Namespace = definitions[name]
        ?: parent?.get(name)
        ?: throw RuntimeException("Undefined: '$name'")

    override fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: Scope): Scope {
        val def = definitions[name]
        return when (def) {
            null -> ((parent ?: RootContext) as Scope).evalSymbol(name, children, parameterContext)
            is Callable -> def.call(null, children, parameterContext)
            is Scope -> def
            else -> throw IllegalStateException("Symbol '$name' defined as  '$def' is not evaluable.")
        }
    }



    override fun toString() =
        buildString {
            for ((name, definition) in definitions) {
                when (definition) {
                    is FunctionDefinition -> {
                        append("fn $name$definition\n")
                    }
                }
            }
        }

}