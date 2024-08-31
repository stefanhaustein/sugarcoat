package org.kobjects.sugarcoat.ast

class FieldDefinition(val type: Type): Definition {
    override fun addDefinition(name: String, value: Definition) {
        throw IllegalArgumentException("Adding $value to fields is not supported.")
    }

}