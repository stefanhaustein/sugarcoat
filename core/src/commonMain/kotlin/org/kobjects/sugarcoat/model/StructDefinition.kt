package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.fn.ParameterConsumer
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.base.Typed
import org.kobjects.sugarcoat.datatype.NativeFunction
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class StructDefinition(
    parent: Classifier,
    fallback: Classifier,
    name: String,
    val constructorName: String = "create"
): ResolvedType, Classifier(parent, name, fallback) {

    val fields = mutableMapOf<String, FieldDefinition>()

    init {
        addChild(StructConstructor(this, constructorName))
    }

    override fun addField(name: String, type: Type, defaultExpression: Expression?) {
        fields[name] = FieldDefinition(this, name, type, defaultExpression)
        addChild(NativeFunction(this, false, type, name, emptyArray()) {
            (it.list[0] as StructInstance).fields[name] ?: throw IllegalStateException("Missing field value for $name")
        })
        addChild(NativeFunction(this, false, VoidType, "set_$name", arrayOf("value" to type)) {
            (it.list[0] as StructInstance).fields[name] = it.list[1]
            Unit
        })
    }

    override fun toString() = "struct $name"

    override fun serialize(sb: StringBuilder) {
        sb.append("struct $name\n")
        serializeBody(sb)
    }

    class StructConstructor(override val parent: StructDefinition, override val name: String) : Callable, Classifier(parent, name), Typed {
        override val static: Boolean
            get() = true

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

            for (definition in parent.fields.values) {
                instance.fields[definition.name] = parameterConsumer.read(parameterScope, definition.name, definition.type)
            }

            println("struct instance created: $instance")

            return instance
        }

        override val type: Type
        get() = FunctionType(
            parent.definitions.values.filterIsInstance<FieldDefinition>().map { it.type },
            parent
        )

    }
}