package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

open class TraitDefinition(
    parent: Classifier,
    fallback: Classifier,
    name: String
) : Type, Classifier(parent, name, emptyList(), fallback) {

    override fun serialize(writer: CodeWriter) {
        writer.append("trait $name\n")
        serializeBody(writer)
    }

    override fun toString() = "trait $name"

    override fun matchImpl(
        other: Type,
        genericTypeResolver: GenericTypeResolver?,
        lazyMessage: () -> String
    ) {
       require(other == this, lazyMessage)
    }

    override fun equals(other: Any?): Boolean {
        return other is TraitDefinition && other.name == name && other.parent == parent
    }

}