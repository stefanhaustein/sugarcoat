package org.kobjects.sugarcoat.type

data class GenericType(val name: String) : Type {

    override fun matchImpl(
        other: Type,
        genericTypeResolver: GenericTypeResolver?,
        lazyMessage: () -> String
    ) {
        if (genericTypeResolver != null) {
            val thisResolved = resolveGenerics(genericTypeResolver)
            when (thisResolved) {
                this -> {
                    // Unresolved; resolve!
                    genericTypeResolver.map[this] = other
                }
                is GenericType -> {
                    println("Generic type $this resolved to another generic type: $thisResolved")
                }
                else -> {
                    // Already known, see if it's compatible.
                    thisResolved.match(other, genericTypeResolver, lazyMessage)
                }
            }
        }
    }

    override fun resolveGenerics(state: GenericTypeResolver): Type {
        return state.map[this] ?: this
    }

    override fun getGenericTypes() = listOf(this)
}