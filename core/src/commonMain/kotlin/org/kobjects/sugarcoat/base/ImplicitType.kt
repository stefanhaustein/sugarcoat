package org.kobjects.sugarcoat.base

class ImplicitType(val resolveFn: () -> ResolvedType) : Type {
    override fun resolve() = resolveFn()
}