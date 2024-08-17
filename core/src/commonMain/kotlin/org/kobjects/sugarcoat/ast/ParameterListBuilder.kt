package org.kobjects.sugarcoat.ast


class ParameterListBuilder {
    val list = mutableListOf<ParameterReference>()

    fun add(value: Node) = add("", value)

    fun add(name: String, value: Node) = add(ParameterReference(name, value))

    fun add(parameter: ParameterReference) {
        if (parameter.name.isEmpty()) {
            require(list.isEmpty() || list.last().name.isEmpty()) { "Can't add unnamed parameters after named parameters."}
        } else {
            require(list.isEmpty() || list.last().name.equals(parameter.name) || !list.any { it.name == parameter.name } )
        }
        list.add(parameter)
    }

    fun build() = list.toList()
}