package org.kobjects.sugarcoat.base

interface ResolvedType : Type {
    override fun resolve() = this
}