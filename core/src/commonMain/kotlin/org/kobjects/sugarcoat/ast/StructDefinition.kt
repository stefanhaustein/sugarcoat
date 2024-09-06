package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RuntimeContext
import org.kobjects.sugarcoat.runtime.StructInstance

class StructDefinition: Callable, ResolvedType, Definition {

    val definitions = mutableMapOf<String, Definition>()

    override fun call(
        receiver: RuntimeContext,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        val parameterConsumer = ParameterConsumer(children)
        val instance = StructInstance(receiver, this)

        for ((name, definition) in definitions) {
            if (definition is FieldDefinition) {
                instance.fields[name] = parameterConsumer.read(parameterContext, name, definition.type)
            }
        }

        println("struct instance created: $instance")

        return instance
    }



    override fun addDefinition(name: String, value: Definition) {
        definitions[name] = value
    }


}