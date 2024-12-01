package org.kobjects.sugarcoat.type

data class GenericTypeResolver(val errorPrefix: () -> String = {""}) {
    val map = mutableMapOf<GenericType, Type>()

    override fun toString() = "GenericTypeResolverState:$map"

    fun resolveTopLevel(type: Type): Type = when(type) {
        is GenericType -> map[type] ?: type
        else -> type
    }

    fun resolveTopLevelOrNull(type: Type?): Type? = when(type) {
        null -> null
        else -> resolveTopLevel(type)
    }

    fun match(type: Type, genericType: GenericType, messagePrefix: () -> String): Type {
        throw UnsupportedOperationException()
    }
}