package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.base.Type

data class ParameterDefinition(
    val name: String,
    val type: Type,
    val repeated: Boolean = false,
) {
    override fun toString() = "$name: $type"
}