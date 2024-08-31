package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RuntimeContext

class StructDefinition: Callable, ResolvedType, Definition {

    val definitions = mutableMapOf<String, Definition>()

    override fun call(
        receiver: RuntimeContext,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        TODO("Not yet implemented")
    }



    override fun addDefinition(name: String, value: Definition) {
        definitions[name] = value
    }


}