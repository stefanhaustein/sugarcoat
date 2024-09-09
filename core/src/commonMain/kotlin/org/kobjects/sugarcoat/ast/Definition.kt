package org.kobjects.sugarcoat.ast

interface Definition {
    val parent: Definition?

    fun addDefinition(name: String, value: Definition)
}