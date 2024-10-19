package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Element
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.fn.ParameterConsumer
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.base.Typed
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class StructDefinition(
    parent: Classifier,
    name: String,
    val constructorName: String = "create"
): ResolvedType, Classifier(parent, name) {

    override fun resolve(name: String): Element {
        if (name == constructorName) {
            return StructConstructor(this, constructorName)
        }
        return super<Classifier>.resolve(name)
    }

    override fun toString() = "struct $name"

    override fun serialize(sb: StringBuilder) {
        sb.append("struct $name\n")
        serializeBody(sb)
    }

    class StructConstructor(override val parent: StructDefinition, override val name: String) : Callable, Classifier(parent, name), Typed {
        override fun toString() = "Constructor $name for $parent"

        override fun serialize(sb: StringBuilder) {
            throw UnsupportedOperationException()
        }

        override fun call(
            receiver: Any?,
            children: List<ParameterReference>,
            parameterScope: LocalRuntimeContext
        ): Any {
            val parameterConsumer = ParameterConsumer(children)
            val instance = StructInstance(parent)

            for (definition in parent.definitions.values) {
                if (definition is FieldDefinition) {
                    instance.fields[definition.name] = parameterConsumer.read(parameterScope, definition.name, definition.type)
                }
            }

            println("struct instance created: $instance")

            return instance
        }

        override val type: Type
        get() = FunctionType(parent.definitions.values.filterIsInstance<FieldDefinition>().map { it.type }, parent)

    }
}