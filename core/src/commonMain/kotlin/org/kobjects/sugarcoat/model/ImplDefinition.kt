package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Element
import org.kobjects.sugarcoat.base.Type

class ImplDefinition(
    parent: Element,
    val trait: Type,
    val struct: Type
) : Classifier(parent, "") {
    override fun resolve() = trait.resolve()

    override fun toString() = "impl $trait for $struct"

    override fun serialize(sb: StringBuilder) {
        sb.append("impl $trait for $struct\n")
        serializeBody(sb)
    }
}