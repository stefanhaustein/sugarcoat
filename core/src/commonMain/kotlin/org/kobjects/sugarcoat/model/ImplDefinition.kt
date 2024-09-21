package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Definition
import org.kobjects.sugarcoat.base.Type

class ImplDefinition(
    parent: Definition,
    val trait: Type,
    val struct: Type
) : AbstractClassifierDefinition(parent, "$trait for $struct") {

}