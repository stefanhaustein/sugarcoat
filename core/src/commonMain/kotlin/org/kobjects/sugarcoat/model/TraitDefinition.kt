package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Element
import org.kobjects.sugarcoat.base.ResolvedType

class TraitDefinition(
    parent: Element,
    name: String
) : ResolvedType, Classifier(parent, name) {

    override fun serialize(sb: StringBuilder) {
        sb.append("trait $name\n")
        serializeBody(sb)
    }

    override fun toString() = "trait $name"
}