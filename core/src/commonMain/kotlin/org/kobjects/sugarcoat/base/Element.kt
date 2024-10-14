package org.kobjects.sugarcoat.base

import org.kobjects.sugarcoat.model.ImplDefinition

interface Element {
    val parent: Element?
    val name: String

    fun addChild(value: Element): Unit = throw UnsupportedOperationException()

    fun resolveOrNull(name: String): Element? = parent?.resolveOrNull(name)

    fun resolve(name: String): Element {
        val result = resolveOrNull(name)
        if (result != null) {
            return result
        }
        if (parent != null) {
            try {
                parent!!.resolve(name)
            } catch (e: Exception) {
                throw IllegalStateException("Unable to resolve '$name' in ${this.name}", e)
            }
        }
        throw IllegalStateException("Unable to resolve '$name' in ${this.name}")
    }

    fun findImpl(source: ResolvedType, target: ResolvedType): ImplDefinition {
        try {
            return parent!!.findImpl(source, target)
        } catch (e: Exception) {
            throw RuntimeException("Unable to map '$source' to '$target' in $this")
        }
    }

    fun serialize(sb: StringBuilder)
}