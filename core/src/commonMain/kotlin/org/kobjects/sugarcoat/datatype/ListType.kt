package org.kobjects.sugarcoat.datatype

object ListType : NativeType("List") {


    data class Instance(
        val value: List<Any>
    ) : NativeInstance() {
        override val type: NativeType
            get() = ListType


    }
}