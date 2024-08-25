package org.kobjects.sugarcoat.ast

interface ResolvedType : Type {
    override fun resolve() = this
}