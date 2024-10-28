package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.type.Type

class ImplInstance (
    val implDefinition: ImplDefinition,
    val wrapped: Any
): Instance {
    override val type: Type
        get() = implDefinition.trait
}