package org.kobjects.sugarcoat.type

data class GenericType(val name: String) : Type {

    override fun matchImpl(
        other: Type,
        genericTypeResolver: GenericTypeResolver?,
        lazyMessage: () -> String
    ) {
        if (genericTypeResolver != null) {
            val resolved = resolveGenerics(genericTypeResolver)
            if (resolved !is GenericType) {
                resolved.match(other, genericTypeResolver, lazyMessage)
            }
        }
    }

    override fun resolveGenerics(state: GenericTypeResolver): Type {
        return state.map[this] ?: this
    }

}