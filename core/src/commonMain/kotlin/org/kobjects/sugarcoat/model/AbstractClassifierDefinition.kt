package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.RootContext
import org.kobjects.sugarcoat.base.Type

abstract class AbstractClassifierDefinition(
    override val parent: Namespace?,
    override val name: String
): Namespace, Type {
    val definitions = mutableMapOf<String, Namespace>()

    override fun addDefinition(value: Namespace) {
        if (value.name.isEmpty()) {
            parent!!.addDefinition(value)
        } else {
            require(!definitions.contains(value.name)) { "Symbol defined already: '${value.name}'" }
            definitions[value.name] = value
        }
    }

    override fun resolve(name: String) =
        definitions[name] ?: parent?.resolve(name)


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