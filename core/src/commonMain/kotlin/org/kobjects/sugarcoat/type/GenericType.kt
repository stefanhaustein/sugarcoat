package org.kobjects.sugarcoat.type

data class GenericType(val name: String) : Type {

    override fun matchImpl(
        other: Type,
        genericTypeResolver: GenericTypeResolver,
        lazyMessage: () -> String
    ): Type {
        val resolved = genericTypeResolver.resolveTopLevel(this)
        return when (resolved) {
            is GenericType -> {
                genericTypeResolver.map[this] = other
                other
            }
            else -> resolved.match(other, genericTypeResolver, lazyMessage)
        }
    }

    override fun resolveGenerics(state: GenericTypeResolver, expected: Type?): Type? {
        val resolved = state.map[this]

        // HACK... In "resolveGenerics", check if the expected.isGeneric(); if so, ignore it and hand to "impl" call
        if (expected is GenericType) {
            return null
        }

        require(resolved == null || expected == null || resolved == expected) {
            "${state.errorPrefix()}: $name already mapped to $resolved in $state; expected: $expected"
        }

        if (resolved != null) {
            return resolved
        }

        if (expected != null) {
            state.map[this] = expected
        }
        return expected
    }

}