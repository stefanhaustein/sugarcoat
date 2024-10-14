package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Element
import org.kobjects.sugarcoat.base.Type

abstract class Classifier(
    override val parent: Element?,
    override val name: String
): Element, Type {
    val definitions = mutableMapOf<String, Element>()

    override fun addChild(value: Element) {
        if (value.name.isEmpty()) {
            parent!!.addChild(value)
        } else {
            require(!definitions.contains(value.name)) { "Symbol defined already: '${value.name}'" }
            definitions[value.name] = value
        }
    }

    override fun resolveOrNull(name: String) =
        definitions[name] ?: parent?.resolveOrNull(name)


    override fun resolve(name: String): Element {
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