package org.kobjects.sugarcoat.type

data class GenericType(val name: String) : Type {

    override fun matches(other: Type) = true

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