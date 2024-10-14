package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Element
import org.kobjects.sugarcoat.base.ResolvedType

class ObjectDefinition(parent: Element, name: String) : ResolvedType, Classifier(parent, name) {
    override fun toString() = "object $name"

    override fun serialize(sb: StringBuilder) {
        sb.append("object $name\n")
        serializeBody(sb)
    }

}