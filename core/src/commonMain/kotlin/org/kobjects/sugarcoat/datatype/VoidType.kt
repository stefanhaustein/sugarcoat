package org.kobjects.sugarcoat.datatype

object VoidType : NativeType("Void") {


    object Instance : NativeInstance() {
        override val type: NativeType
            get() = VoidType
    }
}