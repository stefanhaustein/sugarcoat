package org.kobjects.sugarcoat.base

interface Definition {
    val parent: Definition?

    fun addDefinition(name: String, value: Definition)
}