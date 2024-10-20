package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.Type

abstract class NativeType(
    name: String,
    override val parent: Classifier? = null

) : Classifier(parent, name), ResolvedType {

    override fun toString(): String = "native $name"

    override fun serialize(sb: StringBuilder) {
        sb.append("native type $name\n")
        serializeBody(sb)
    }
}