package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.type.Type

class DeGenerifiedClassifierProxy(
    override val original: Classifier,
    typeParameters: List<Type>,
) : Classifier(
    original.parent,
    original.name + typeParameters,
    typeParameters,
    original.fallback
) {



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