package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.ResolvedType

class ObjectDefinition(parent: Classifier, fallback: Classifier, name: String) : ResolvedType, Classifier(parent, name, fallback) {
    override fun toString() = "object $name"

    override fun serialize(sb: StringBuilder) {
        sb.append("object $name\n")
        serializeBody(sb)
    }

}