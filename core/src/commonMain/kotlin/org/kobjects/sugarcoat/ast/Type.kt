package org.kobjects.sugarcoat.ast

interface Type {
    fun resolve(): ResolvedType
}