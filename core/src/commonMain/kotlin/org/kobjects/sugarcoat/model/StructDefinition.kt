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

    val fields = mutableMapOf<String, FieldDefinition>()

    override fun addField(name: String, type: Type?, defaultExpression: Expression?) {
        fields[name] = FieldDefinition(name, type, defaultExpression)
    }

    override fun resolveImpliedMethods() {
        val resolvedFields = fields.values.map { it.resolve(this) }
        for(field in resolvedFields) {
            fields[field.name] = field
            addNativeMethod(field.type!!, field.name) {
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

            for ((i, definition) in parent.fields.values.withIndex()) {
                instance.fields[definition.name] = children[i]!!.eval(parameterScope)
            }

            println("struct instance created: $instance")

            return instance
        }

        override val type: FunctionType
        get() = FunctionType(
            parent,
            parent.fields.values.map { ParameterDefinition(it.name, it.type!!) }
        )

    }
}