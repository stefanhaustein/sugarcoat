package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Scope

class ImplInstance (
    val implDefinition: ImplDefinition,
    val wrapped: Scope
): Instance {
    override val type: AbstractClassifierDefinition
        get() = implDefinition
}