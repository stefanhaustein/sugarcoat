package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.model.Namespace
import org.kobjects.sugarcoat.type.GenericType

abstract class AbstractFunctionDefinition(
    parent: Namespace?,
    name: String,
    open val typeParameters: List<GenericType> = emptyList(),
    fallback: Namespace? = null
) : Namespace(parent, name, fallback), Callable {
}