package org.kobjects.sugarcoat.ast

class TypeReference(val name: String) : Type {
    override fun resolve() = throw UnsupportedOperationException()

    override fun toString() = name
}