package org.kobjects.sugarcoat.datatype

class NativeArgList(val list: List<Any>) {
    fun bool(index: Int) = list[index] as Boolean
    fun f64(index: Int) = list[index] as Double
    fun i64(index: Int) = list[index] as Long
}