package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

open class TraitDefinition(
    parent: Namespace,
    fallback: Namespace,
    name: String,
    typeParameters: List<Type> = emptyList(),
    raw: TraitDefinition? = null,
) : Classifier(parent, name, typeParameters, fallback) {

    override val raw: TraitDefinition = raw ?: this

    override fun serialize(writer: CodeWriter) {
        writer.append("trait $name\n")
        serializeBody(writer)
    }

    override fun toString() = "trait $name"

    override fun equals(other: Any?): Boolean {
        return other is TraitDefinition && other.name == name && other.parent == parent
    }


    override fun typed(vararg resolvedTypes: Type) =
        super.typed(*resolvedTypes) as TraitDefinition

}