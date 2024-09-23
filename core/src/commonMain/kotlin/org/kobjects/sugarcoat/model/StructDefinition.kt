package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.fn.ParameterConsumer
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Scope

class StructDefinition(
    parent: Namespace,
    name: String,
    val constructorName: String = "create"
): ResolvedType, AbstractClassifierDefinition(parent, name) {

    override fun evalSymbol(
        name: String,
        children: List<ParameterReference>,
        parameterContext: Scope
    ): Scope {
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