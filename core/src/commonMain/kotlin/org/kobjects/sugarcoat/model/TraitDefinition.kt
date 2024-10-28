package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.type.Type

class TraitDefinition(
    parent: Classifier,
    fallback: Classifier,
    name: String
) : Type, Classifier(parent, name, fallback) {

    override fun serialize(sb: StringBuilder) {
        sb.append("trait $name\n")
        serializeBody(sb)
    }

    override fun toString() = "trait $name"

}