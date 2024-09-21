package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.model.Instance

abstract class NativeInstance : Instance {
    override abstract val type: NativeType

}