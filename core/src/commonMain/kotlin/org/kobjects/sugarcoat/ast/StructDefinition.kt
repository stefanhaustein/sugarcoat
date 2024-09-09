package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RootContext
import org.kobjects.sugarcoat.runtime.RuntimeContext
import org.kobjects.sugarcoat.runtime.StructInstance

class StructDefinition(
    parent: Definition,
    name: String,
    val constructorName: String = "create"): ResolvedType, AbstractClassifierDefinition(parent, name) {

    override fun addDefinition(name: String, value: Definition) {
        super.addDefinition(name, value)
    }

    override fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        return if (name == constructorName) {
            val parameterConsumer = ParameterConsumer(children)
            val instance = StructInstance(this, this)

            for ((name, definition) in definitions) {
                if (definition is FieldDefinition) {
                    instance.fields[name] = parameterConsumer.read(parameterContext, name, definition.type)
                }
            }

            println("struct instance created: $instance")

            return instance
        } else super.evalSymbol(name, children, parameterContext)
    }



}