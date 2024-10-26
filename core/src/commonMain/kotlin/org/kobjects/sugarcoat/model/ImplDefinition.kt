package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.Type

class ImplDefinition(
    parent: Classifier,
    fallback: Classifier,
    var trait: Type,
    var struct: Type
) : Classifier(parent, "", fallback), ResolvedType {
//    override fun resolve() = trait.resolve()

    override fun resolveTypes() {
        super.resolveTypes()
        trait = trait.resolve(this)
        struct = struct.resolve(this)
    }

    override fun toString() = "impl ($trait) for ($struct)"

    override fun serialize(sb: StringBuilder) {
        sb.append("impl $trait for $struct\n")
        serializeBody(sb)
    }
}