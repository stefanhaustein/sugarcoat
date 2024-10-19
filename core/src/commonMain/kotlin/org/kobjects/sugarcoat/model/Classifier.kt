package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.Type

abstract class Classifier(
    open val parent: Classifier?,
    open val name: String,
    open val fallback: Classifier? = null
) {
    val definitions = mutableMapOf<String, Classifier>()
    val unnamed = mutableListOf<Classifier>()

    open fun addChild(value: Classifier) {
        if (value.name.isEmpty()) {
            unnamed.add(value)
        } else {
            require(!definitions.contains(value.name)) { "Symbol defined already: '${value.name}'" }
            definitions[value.name] = value
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

    fun collectImpls(impls: MutableList<ImplDefinition>) {
        if (this is ImplDefinition) {
            impls.add(this)
        }
        for ((_, classifier) in definitions) {
            classifier.collectImpls(impls)
        }
        for (classifier in unnamed) {
            classifier.collectImpls(impls)
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