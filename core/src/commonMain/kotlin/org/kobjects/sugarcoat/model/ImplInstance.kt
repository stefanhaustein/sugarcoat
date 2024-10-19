package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Type

class ImplInstance (
    val implDefinition: ImplDefinition,
    val wrapped: Any
): Instance {
    override val type: Type
        get() = implDefinition
}