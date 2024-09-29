package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.fn.FunctionDefinition
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

    override fun resolveOrNull(name: String) =
        definitions[name] ?: parent?.resolveOrNull(name)


    override fun resolve(name: String): Namespace {
        val result = resolveOrNull(name)
        if (result != null) {
            return result
        }
        if (parent != null) {
            try {
                parent!!.resolve(name)
            } catch (e: Exception) {
                throw IllegalStateException("Unable to resolve '$name' in ${this.name} containing ${definitions.keys}", e)
            }
        }
        throw IllegalStateException("Unable to resolve '$name' in ${this.name} containing ${definitions.keys}")
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