package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.base.ResolvedType

class ObjectDefinition(parent: Namespace, name: String) : ResolvedType, AbstractClassifierDefinition(parent, name) {
    override fun toString() = "object $name"

    override fun serialize(sb: StringBuilder) {
        sb.append("object $name\n")
        serializeBody(sb)
    }

}