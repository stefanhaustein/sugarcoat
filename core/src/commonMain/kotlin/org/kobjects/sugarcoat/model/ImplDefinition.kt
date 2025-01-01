package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.DelegateToImpl
import org.kobjects.sugarcoat.type.Type

class ImplDefinition(
    parent: Namespace,
    fallback: Namespace,
    var trait: Type,
    var wrapped: Type
) : Namespace(parent, "", emptyList(), fallback) {
//    override fun resolve() = trait.resolve()

    override fun resolveSignatures() {
        trait = trait.resolveType(this)
        wrapped = wrapped.resolveType(this)
    }

    override fun resolveImpls(program: Program) {
        require(trait is TraitDefinition) {
            "Error: '$trait' is not a Trait"
        }
        for (traitMember in (trait as TraitDefinition).definitions.values) {
            if (traitMember is DelegateToImpl) {
                var localMethod = definitions[traitMember.name]
                if (localMethod == null) {
                    val wrappedMethod = (wrapped as Namespace).definitions[traitMember.name]
                    if (wrappedMethod != null) {
                        definitions[traitMember.name] = wrappedMethod
                        localMethod = wrappedMethod
                    }
                }

                if (localMethod == null){
                    throw IllegalStateException("""Missing method in "impl '$trait' for '$wrapped'": $traitMember""")
                }
                require(localMethod is Callable) {
                    "Trait method impl $localMethod must be a method."
                }
                localMethod.type.match(traitMember.type) {
                    "Signature ${localMethod.type} for ${localMethod.name} does not match trait method signature ${traitMember.type}"
                }
            }
        }



        program.impls[wrapped to (trait as TraitDefinition)] = this
    }

    override fun toString() = "impl ($trait) for ($wrapped)"

    override fun selfType() = wrapped

    override fun serialize(writer: CodeWriter) {
        writer.append("impl $trait for $wrapped")
        serializeBody(writer)
    }
}