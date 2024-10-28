package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.type.Type

class GlobalRuntimeContext(
    val program: Program,
    val printFn: (String) -> Unit = ::print
) {
    val implementations: Map<Pair<Type, TraitDefinition>, ImplDefinition>

    init {
        val builder = mutableListOf<ImplDefinition>()
        program.collectImpls(builder)
        implementations =
            builder.associateBy { it.wrapped to (it.trait as TraitDefinition) }

    }


    fun findImpl(source: Type, target: Type): ImplDefinition {
        return implementations[source to target] ?: throw IllegalStateException("No impl found that maps $source to $target; available: ${implementations.keys}")
    }

}
