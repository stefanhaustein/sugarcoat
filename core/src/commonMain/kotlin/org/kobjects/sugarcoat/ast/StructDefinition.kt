package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RuntimeContext

class StructDefinition(

): Callable, ResolvedType {
    override fun call(
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        TODO("Not yet implemented")
    }

    val fields = mutableMapOf<String, Type>()
    val methods = mutableMapOf<String, FunctionDefinition>()

    fun addField(name: String, type: Type) {
        fields[name] = type
    }
}