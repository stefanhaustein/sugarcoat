package org.kobjects.sugarcoat.base

import org.kobjects.sugarcoat.model.ImplDefinition

interface Namespace {
    val parent: Namespace?
    val name: String

    fun addDefinition(value: Namespace): Unit = throw UnsupportedOperationException()

    fun get(name: String): Namespace = parent!!.get(name)

    fun findImpl(source: ResolvedType, target: ResolvedType): ImplDefinition = parent!!.findImpl(source, target)
}