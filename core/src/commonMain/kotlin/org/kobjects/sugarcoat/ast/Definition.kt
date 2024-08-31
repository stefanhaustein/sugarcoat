package org.kobjects.sugarcoat.ast

interface Definition {
    fun addDefinition(name: String, value: Definition)
}