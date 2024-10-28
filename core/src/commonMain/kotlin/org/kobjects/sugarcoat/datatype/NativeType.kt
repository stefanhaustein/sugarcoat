package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.type.Type

abstract class NativeType(
    name: String,
    override val parent: Classifier? = null

) : Classifier(parent, name), Type {

    override fun toString(): String = "native $name"

    override fun serialize(sb: StringBuilder) {
        sb.append("native type $name\n")
        serializeBody(sb)
    }
}