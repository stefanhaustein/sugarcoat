package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ResolutionContext
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.type.GenericType

class StructDefinition(
    parent: Namespace,
    fallback: Namespace,
    name: String,
    genericType: List<GenericType>,
    override val constructorName: String = "create"
): Type, Namespace(parent, name, genericType, fallback) {

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

        addChild(StructConstructor(this))
    }


    override fun serialize(writer: CodeWriter) {
        writer.newline()
        writer.append("struct $name")
        writer.indent()
        serializeBody(writer)
        writer.outdent()
    }

    override fun selfType() = this

    override fun toString() = "struct $name"


    class StructConstructor(override val parent: StructDefinition) : Callable, Namespace(parent, "create") {
        override val static: Boolean
            get() = true

        override fun toString() = "Constructor $name for $parent"

        override fun serialize(writer: CodeWriter) {
            writer.append("# create")
            writer.newline()
        }

        override fun call(
            receiver: Any?,
            children: List<Expression?>,
            parameterScope: LocalRuntimeContext
        ): Any {

            val instance = StructInstance(parent)

            for ((i, definition) in type.parameterTypes.withIndex()) {
                instance.fields[definition.name] = children[i]?.eval(parameterScope) ?: definition.defaultValue?.eval(parameterScope) ?: throw IllegalStateException("Missing parameter: $definition")
            }

            // println("struct instance created: $instance")

            return instance
        }

        override var type: FunctionType = FunctionType(
            parent,
            parent.instanceFields.values.map  { ParameterDefinition(it.name, it.type, false, it.unresolvedDefaultExpression) }
        )

        override fun resolveExpressions() {
            type = type.resolveDefaultExpressions(ResolutionContext(parent))
        }

    }
}