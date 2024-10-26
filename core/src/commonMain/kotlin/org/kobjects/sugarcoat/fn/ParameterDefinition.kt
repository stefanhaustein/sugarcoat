package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.model.Classifier

data class ParameterDefinition(
    val name: String,
    val type: Type,
    val repeated: Boolean = false,
) {
    override fun toString() = "$name: $type"

    fun resolve(context: Classifier) = copy(type = type.resolve(context))
}