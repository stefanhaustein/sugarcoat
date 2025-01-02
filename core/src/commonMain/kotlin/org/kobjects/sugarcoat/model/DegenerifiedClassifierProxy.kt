package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.fn.AbstractFunctionDefinition
import org.kobjects.sugarcoat.fn.DegenerifiedFunctionProxy
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

class DegenerifiedClassifierProxy(
    val original: Classifier,
    val resolvedTypes: List<Type>
) : Classifier(original.parent, original.name + resolvedTypes, emptyList(), original.fallback

) {
    init {
        require(resolvedTypes.size == original.typeParameters.size) {
            "${original.typeParameters.size} types expected to resolve ${original.typeParameters} in $original, but got $resolvedTypes"
        }
        val genericTypeResolver = GenericTypeResolver()

        for (i in original.typeParameters.indices) {
            genericTypeResolver.map[original.typeParameters[i]] = resolvedTypes[i]
        }

        for (member in original.definitions.values) {
            if (member is AbstractFunctionDefinition) {
                val resolvedFunction = DegenerifiedFunctionProxy.create(this, member, genericTypeResolver)
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