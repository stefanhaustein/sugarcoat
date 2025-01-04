package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.type.Type

class DeGenerifiedClassifierProxy(
    override val raw: Classifier,
    typeParameters: List<Type>,
) : Classifier(
    raw.parent,
    raw.name + typeParameters,
    typeParameters,
    raw.fallback
) {



    override val constructorName: String
        get() = raw.constructorName

    override fun serialize(writer: CodeWriter) {
        writer.newline()
        writer.append("$name")
        writer.indent()
        serializeBody(writer)
        writer.outdent()
    }

    override fun toString() = name

}