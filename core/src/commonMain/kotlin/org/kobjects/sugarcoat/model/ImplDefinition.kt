package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.type.Type

class ImplDefinition(
    parent: Classifier,
    fallback: Classifier,
    var trait: Type,
    var wrapped: Type
) : Classifier(parent, "", fallback) {
//    override fun resolve() = trait.resolve()

    override fun resolveSignatures() {
        trait = trait.resolveType(this)
        wrapped = wrapped.resolveType(this)
    }

    override fun resolveImpls(program: Program) {
        program.impls[wrapped to (trait as TraitDefinition)] = this
    }

    override fun toString() = "impl ($trait) for ($wrapped)"

    override fun selfType() = wrapped

    override fun serialize(writer: CodeWriter) {
        writer.append("impl $trait for $wrapped")
        serializeBody(writer)
    }
}