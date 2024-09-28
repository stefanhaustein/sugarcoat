package org.kobjects.sugarcoat.base

import org.kobjects.sugarcoat.model.ImplDefinition

interface Namespace {
    val parent: Namespace?
    val name: String

    fun addDefinition(value: Namespace): Unit = throw UnsupportedOperationException()

    fun resolveOrNull(name: String): Namespace? = parent?.resolveOrNull(name)

    fun resolve(name: String): Namespace {
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

    fun findImpl(source: ResolvedType, target: ResolvedType): ImplDefinition =
        parent!!.findImpl(source, target)
}