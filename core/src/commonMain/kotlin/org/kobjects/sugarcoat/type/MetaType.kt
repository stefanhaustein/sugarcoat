package org.kobjects.sugarcoat.type

import org.kobjects.sugarcoat.model.Namespace

data class MetaType(val type: Namespace) : Type {
    override fun getGenericTypes(): Set<GenericType> = if (type is Type) type.getGenericTypes() else emptySet()
}
