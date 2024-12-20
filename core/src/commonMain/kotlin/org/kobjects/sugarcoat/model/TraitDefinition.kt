package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.type.Type

open class TraitDefinition(
    parent: Classifier,
    fallback: Classifier,
    name: String
) : Type, Classifier(parent, name, fallback) {

    override fun serialize(writer: CodeWriter) {
        writer.append("trait $name\n")
        serializeBody(writer)
    }

    override fun toString() = "trait $name"

}