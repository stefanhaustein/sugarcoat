package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.type.Type

class ObjectDefinition(parent: Classifier, fallback: Classifier, name: String) : Type, Classifier(parent, name, emptyList(), fallback) {
    override fun toString() = "object $name"

    override fun serialize(writer: CodeWriter) {
        writer.newline()
        writer.append("object $name")
        writer.indent()
        serializeBody(writer)
        writer.outdent()
    }

}