package org.kobjects.sugarcoat.ast

class ImplDefinition( val trait: Type, val struct: Type) : Definition {
    val definitions = mutableMapOf<String, Definition>()

    override fun addDefinition(name: String, value: Definition) {
        definitions[name] = value
    }
}