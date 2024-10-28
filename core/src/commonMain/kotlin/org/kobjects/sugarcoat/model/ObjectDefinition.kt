package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.type.Type

class ObjectDefinition(parent: Classifier, fallback: Classifier, name: String) : Type, Classifier(parent, name, fallback) {
    override fun toString() = "object $name"

    override fun serialize(sb: StringBuilder) {
        sb.append("object $name\n")
        serializeBody(sb)
    }

}