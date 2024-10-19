package org.kobjects.sugarcoat.base

import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.ImplDefinition
import org.kobjects.sugarcoat.model.Program
import org.kobjects.sugarcoat.model.TraitDefinition

class GlobalRuntimeContext(
    val program: Program,
    val printFn: (String) -> Unit = ::print
) {
    val implementations: Map<Pair<ResolvedType, TraitDefinition>, ImplDefinition>

    init {
        val builder = mutableListOf<ImplDefinition>()
        program.collectImpls(builder)
        implementations =
            builder.associateBy { it.struct.resolve() to (it.trait.resolve() as TraitDefinition) }

    }


    fun findImpl(source: ResolvedType, target: ResolvedType): ImplDefinition {
        return implementations[source to target] ?: throw IllegalStateException("No impl found that maps $source to $target; available: ${implementations.keys}")
    }

}
