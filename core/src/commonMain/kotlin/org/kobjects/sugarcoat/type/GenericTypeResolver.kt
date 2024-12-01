package org.kobjects.sugarcoat.type

data class GenericTypeResolver(val errorPrefix: () -> String = {""}) {
    val map = mutableMapOf<GenericType, Type>()

    override fun toString() = "GenericTypeResolverState:$map"
}