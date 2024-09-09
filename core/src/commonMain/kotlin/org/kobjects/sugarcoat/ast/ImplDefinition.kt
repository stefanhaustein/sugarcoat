package org.kobjects.sugarcoat.ast

class ImplDefinition(
    parent: Definition,
    val trait: Type,
    val struct: Type
) : AbstractClassifierDefinition(parent, "$trait for $struct") {

}