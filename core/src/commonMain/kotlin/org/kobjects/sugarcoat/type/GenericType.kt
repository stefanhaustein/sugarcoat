package org.kobjects.sugarcoat.type

data class GenericType(val name: String) : Type {


    override fun resolveGenerics(state: GenericTypeResolverState, expected: Type?): Type? {
        val resolved = state.map[this]

        require(resolved == null || expected == null || resolved == expected) {
            "${state.errorPrefix}: $name already mapped to $resolved; expected: $expected"
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