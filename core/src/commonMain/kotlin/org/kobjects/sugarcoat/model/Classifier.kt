package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.Type

abstract class Classifier(
    parent: Namespace?,
    name: String,
    genericTypes: List<GenericType> = emptyList(),
    fallback: Namespace? = null
) : Namespace(parent, name, genericTypes, fallback), Type {

}