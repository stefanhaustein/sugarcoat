package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.base.Type

class ImplDefinition(
    parent: Namespace,
    val trait: Type,
    val struct: Type
) : AbstractClassifierDefinition(parent, "") {
    override fun resolve() = trait.resolve()
}