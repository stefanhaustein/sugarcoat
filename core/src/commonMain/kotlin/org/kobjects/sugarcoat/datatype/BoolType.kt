package org.kobjects.sugarcoat.datatype

object BoolType : NativeType( "Bool") {


    data class Instance(val value: Boolean) : NativeInstance() {
        override val type: NativeType
            get() = TODO("Not yet implemented")

        override fun toString() = value.toString()
    }

}