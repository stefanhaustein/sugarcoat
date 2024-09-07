package org.kobjects.sugarcoat.ast

class TraitDefinition : Definition, ResolvedType {

    val definitions = mutableMapOf<String, Definition>()

    override fun addDefinition(name: String, value: Definition) {
       definitions[name] = value
    }
}