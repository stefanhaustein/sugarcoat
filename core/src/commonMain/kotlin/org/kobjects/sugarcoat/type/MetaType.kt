package org.kobjects.sugarcoat.type

import org.kobjects.sugarcoat.model.Classifier

data class MetaType(val type: Classifier) : Type {
    override fun getGenericTypes(): List<GenericType> = if (type is Type) type.getGenericTypes() else emptyList()
}
