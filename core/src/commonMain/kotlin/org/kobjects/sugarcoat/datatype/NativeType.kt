package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.type.Type

abstract class NativeType(
    name: String,
    override val parent: Classifier? = null

) : Classifier(parent, name), Type {

    override val constructorName: String
        get() = if (definitions.contains("create")) "create" else ""

    override fun toString(): String = "native $name"

    override fun serialize(writer: CodeWriter) {
        writer.append("native type $name\n")
        serializeBody(writer)
    }
}