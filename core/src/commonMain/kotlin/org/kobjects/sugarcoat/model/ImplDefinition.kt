package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Type

class ImplDefinition(
    parent: Classifier,
    fallback: Classifier,
    var trait: Type,
    var wrapped: Type
) : Classifier(parent, "", fallback) {
//    override fun resolve() = trait.resolve()

    override fun resolveTypes() {
        super.resolveTypes()
        trait = trait.resolve(this)
        wrapped = wrapped.resolve(this)
    }

    override fun toString() = "impl ($trait) for ($wrapped)"

    override fun selfType() = wrapped

    override fun serialize(sb: StringBuilder) {
        sb.append("impl $trait for $wrapped\n")
        serializeBody(sb)
    }
}