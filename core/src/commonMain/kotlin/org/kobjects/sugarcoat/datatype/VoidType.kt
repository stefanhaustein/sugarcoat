package org.kobjects.sugarcoat.datatype

object VoidType : NativeType("Void") {


    object VoidInstance : NativeInstance() {
        override val type: NativeType
            get() = VoidType
    }
}