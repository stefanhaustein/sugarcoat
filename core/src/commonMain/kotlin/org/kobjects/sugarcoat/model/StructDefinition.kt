package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.fn.TypedCallable

class StructDefinition(
    parent: Classifier,
    fallback: Classifier,
    name: String,
    val constructorName: String = "create"
): Type, Classifier(parent, name, fallback) {

    val instanceFields = mutableMapOf<String, InstanceFieldDefinition>()

    override fun addInstanceField(
        mutable: Boolean,
        name: String,
        type: Type,
        initializer: Expression?
    ) {
        instanceFields[name] = InstanceFieldDefinition(name, type, initializer)
    }

    override fun resolveSignatures() {
        for (field in instanceFields.values) {
            field.resolveType(this)
        }

        for(field in instanceFields.values) {
            addNativeMethod(field.type, field.name) {
                (it.list[0] as StructInstance).fields[field.name]
                    ?: throw IllegalStateException("Missing field value for ${field.name}")
            }
            addNativeMethod(VoidType, "set_${field.name}", ParameterDefinition("value", field.type)) {
                (it.list[0] as StructInstance).fields[field.name] = it.list[1]
                Unit
            }
        }

        addChild(StructConstructor(this, constructorName))
    }

    override fun serialize(sb: StringBuilder) {
        sb.append("struct $name\n")
        serializeBody(sb)
    }

    override fun selfType() = this

    override fun toString() = "struct $name"


    class StructConstructor(override val parent: StructDefinition, override val name: String) : TypedCallable, Classifier(parent, name) {
        override val static: Boolean
            get() = true

        override fun toString() = "Constructor $name for $parent"

        override fun serialize(sb: StringBuilder) {
            throw UnsupportedOperationException()
        }

        override fun call(
            receiver: Any?,
            children: List<Expression?>,
            parameterScope: LocalRuntimeContext
        ): Any {

            val instance = StructInstance(parent)

            for ((i, definition) in parent.instanceFields.values.withIndex()) {
                instance.fields[definition.name] = children[i]!!.eval(parameterScope)
            }

            println("struct instance created: $instance")

            return instance
        }

        override val type: FunctionType

        get() = FunctionType(
            parent,
            parent.instanceFields.values.map { ParameterDefinition(it.name, it.type, false) }
        )

    }
}