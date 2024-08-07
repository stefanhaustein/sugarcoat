package org.kobjects.sugarcoat


class ParameterListBuilder {
    val list = mutableListOf<Parameter>()

    fun add(value: Evaluable) = add("", value)

    fun add(name: String, value: Evaluable) = add(Parameter(name, value))

    fun add(parameter: Parameter) {
        if (parameter.name.isEmpty()) {
            require(list.isEmpty() || list.last().name.isEmpty()) { "Can't add unnamed parameters after named parameters."}
        } else {
            require(list.isEmpty() || list.last().name.equals(parameter.name) || !list.any { it.name == parameter.name } )
        }
        list.add(parameter)
    }

    fun build() = list.toList()
}