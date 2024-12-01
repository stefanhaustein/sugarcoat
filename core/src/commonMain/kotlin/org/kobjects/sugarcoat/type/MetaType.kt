package org.kobjects.sugarcoat.type

import org.kobjects.sugarcoat.model.Classifier

class MetaType(val type: Classifier) : Type {

    override fun matches(other: Type) =
        other is GenericType || (other is MetaType && type == other.type)
}
