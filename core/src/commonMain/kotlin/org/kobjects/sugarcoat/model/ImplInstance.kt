package org.kobjects.sugarcoat.model

class ImplInstance (
    val implDefinition: ImplDefinition,
    val wrapped: Any
): Instance {
    override val type: AbstractClassifierDefinition
        get() = implDefinition
}