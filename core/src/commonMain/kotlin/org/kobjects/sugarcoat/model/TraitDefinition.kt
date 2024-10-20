package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.ResolvedType

class TraitDefinition(
    parent: Classifier,
    fallback: Classifier,
    name: String
) : ResolvedType, Classifier(parent, name, fallback) {

    override fun serialize(sb: StringBuilder) {
        sb.append("trait $name\n")
        serializeBody(sb)
    }

    override fun toString() = "trait $name"
}