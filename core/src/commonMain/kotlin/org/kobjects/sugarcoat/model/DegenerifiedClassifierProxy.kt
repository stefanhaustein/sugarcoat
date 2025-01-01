package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.DegenerifiedFunctionProxy
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

class DegenerifiedClassifierProxy(
    val original: Namespace,
    resolvedTypes: List<Type>
) : Classifier(original.parent, original.name + resolvedTypes, emptyList(), original.fallback

) {
    init {
        require(resolvedTypes.size == original.genericTypes.size) {
            "${original.genericTypes.size} types expected to resolve ${original.genericTypes} in $original, but got $resolvedTypes"
        }
        val genericTypeResolver = GenericTypeResolver()

        for (i in original.genericTypes.indices) {
            genericTypeResolver.map[original.genericTypes[i]] = resolvedTypes[i]
        }

        for (member in original.definitions.values) {
            if (member is Callable) {
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