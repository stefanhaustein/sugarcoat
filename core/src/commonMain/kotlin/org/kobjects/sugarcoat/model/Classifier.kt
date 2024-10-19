package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.Type

abstract class Classifier(
    open val parent: Classifier?,
    open val name: String
) {
    val definitions = mutableMapOf<String, Classifier>()

    open fun addChild(value: Classifier) {
        if (value.name.isEmpty()) {
            parent!!.addChild(value)
        } else {
            require(!definitions.contains(value.name)) { "Symbol defined already: '${value.name}'" }
            definitions[value.name] = value
        }
    }

    open fun findImpl(source: ResolvedType, target: ResolvedType): ImplDefinition {
        try {
            return parent!!.findImpl(source, target)
        } catch (e: Exception) {
            throw RuntimeException("Unable to map '$source' to '$target' in $this")
        }
    }

    fun resolveOrNull(name: String): Classifier? =
        definitions[name] ?: parent?.resolveOrNull(name)


    abstract fun serialize(sb: StringBuilder)

    open fun resolve(name: String): Classifier {
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

    abstract override fun toString(): String

    fun serializeBody(sb: StringBuilder) {
        for ((name, definition) in definitions) {
            sb.append("  ")
            definition.serialize(sb)
        }

    }
    /*=
        buildString {
            for ((name, definition) in definitions) {
                when (definition) {
                    is FunctionDefinition -> {
                        append("fn $name$definition\n")
                    }
                }
            }
        }
*/
}