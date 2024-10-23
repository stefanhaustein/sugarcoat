package org.kobjects.sugarcoat.base

class ResolutionPassType : Type {
    var resolved: Type? = null
    override fun resolve(): ResolvedType = resolved!!.resolve()
}