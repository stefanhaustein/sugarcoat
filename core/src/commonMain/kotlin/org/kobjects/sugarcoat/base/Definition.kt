package org.kobjects.sugarcoat.base

interface Definition {
    val parent: Definition?
    val name: String

    fun addDefinition(value: Definition): Unit = throw UnsupportedOperationException()
}