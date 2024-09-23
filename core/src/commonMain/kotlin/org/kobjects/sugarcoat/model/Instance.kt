package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type

interface Instance{
    val type: AbstractClassifierDefinition

    fun getField(name: String): Any? = null
}