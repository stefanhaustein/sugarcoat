package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.Type

class ImplDefinition(
    parent: Classifier,
    val trait: Type,
    val struct: Type
) : Classifier(parent, ""), ResolvedType {
//    override fun resolve() = trait.resolve()

    override fun toString() = "impl ($trait) for ($struct)"

    override fun serialize(sb: StringBuilder) {
        sb.append("impl $trait for $struct\n")
        serializeBody(sb)
    }
}