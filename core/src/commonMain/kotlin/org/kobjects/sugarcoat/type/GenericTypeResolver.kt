package org.kobjects.sugarcoat.type

data class GenericTypeResolver(val errorPrefix: () -> String = {""}) {
    val map = mutableMapOf<GenericType, Type>()

    override fun toString() = "GenericTypeResolverState:$map"

    fun resolveTopLevel(type: Type): Type = when(type) {
        is GenericType -> map[type] ?: type
        else -> type
    }

    fun resolveTopLevelNullable(type: Type?): Type? = when(type) {
        null -> null
        else -> resolveTopLevel(type)
    }

}