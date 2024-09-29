package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.fn.ParameterConsumer
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.base.Typed
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class StructDefinition(
    parent: Namespace,
    name: String,
    val constructorName: String = "create"
): ResolvedType, AbstractClassifierDefinition(parent, name) {

    override fun resolve(name: String): Namespace {
        if (name == constructorName) {
            return object : Callable, Namespace, Typed {
                override val parent: Namespace
                    get() = this@StructDefinition
                override val name: String
                    get() = constructorName

                override fun serialize(sb: StringBuilder) {
                    throw UnsupportedOperationException()
                }

                override fun call(
                    receiver: Any?,
                    children: List<ParameterReference>,
                    parameterScope: LocalRuntimeContext
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

                override val type: Type
                    get() = FunctionType(definitions.values.filterIsInstance<FieldDefinition>().map { it.type }, this@StructDefinition)

            }
        }
        return super<AbstractClassifierDefinition>.resolve(name)
    }

    override fun toString() = "struct $name"

    override fun serialize(sb: StringBuilder) {
        sb.append("struct $name\n")
        serializeBody(sb)
    }
}