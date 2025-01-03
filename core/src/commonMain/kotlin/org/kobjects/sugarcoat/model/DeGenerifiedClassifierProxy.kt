package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.fn.AbstractFunctionDefinition
import org.kobjects.sugarcoat.fn.DeGenerifiedFunctionProxy
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

class DeGenerifiedClassifierProxy(
    override val original: Classifier,
    genericTypeResolver: GenericTypeResolver,
) : Classifier(
    original.parent,
    original.name + genericTypeResolver.resolveAll(original.typeParameters),
    genericTypeResolver.resolveAll(original.typeParameters),
    original.fallback
) {
    init {
        for (member in original.definitions.values) {
            if (member is AbstractFunctionDefinition) {
                val resolvedFunction = DeGenerifiedFunctionProxy.create(this, member, genericTypeResolver)
                println("Resolved function: $resolvedFunction")
                addChild(resolvedFunction)
            }
        }
    }


    override val constructorName: String
        get() = original.constructorName

    override fun serialize(writer: CodeWriter) {
        writer.newline()
        writer.append("$name")
        writer.indent()
        serializeBody(writer)
        writer.outdent()
    }

    override fun toString() = name

}