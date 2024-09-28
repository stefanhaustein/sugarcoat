package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.fn.ParameterConsumer
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.RuntimeContext

class StructDefinition(
    parent: Namespace,
    name: String,
    val constructorName: String = "create"
): ResolvedType, AbstractClassifierDefinition(parent, name) {

    override fun resolve(name: String): Namespace {
        if (name == constructorName) {
            return object : Callable, Namespace {
                override val parent: Namespace
                    get() = this@StructDefinition
                override val name: String
                    get() = constructorName

                override fun call(
                    receiver: Any?,
                    children: List<ParameterReference>,
                    parameterScope: RuntimeContext
                ): Any {
                    val parameterConsumer = ParameterConsumer(children)
                    val instance = StructInstance(this@StructDefinition)

                    for (definition in definitions.values) {
                        if (definition is FieldDefinition) {
                            instance.fields[definition.name] = parameterConsumer.read(parameterScope, definition.name, definition.type)
                        }
                    }

                    println("struct instance created: $instance")

                    return instance
                }

            }
        }
        return super<AbstractClassifierDefinition>.resolve(name)
    }


}