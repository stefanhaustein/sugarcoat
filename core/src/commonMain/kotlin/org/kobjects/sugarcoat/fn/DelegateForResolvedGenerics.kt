package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type


class DelegateForResolvedGenerics(
    parent: Classifier,
    val wrapped: Classifier,
    override val type: FunctionType,
    genericTypes: List<GenericType>
) : Classifier(parent, wrapped.name, genericTypes), Callable {

    override val static: Boolean
        get() = (wrapped as Callable).static

    companion object {
        fun create(parent: Classifier, original: Classifier, genericTypeResolver: GenericTypeResolver): DelegateForResolvedGenerics {
            require(original is Callable) {
                "Original must be callable for resolving generics."
            }
            val remainingGenericTypes = mutableListOf<GenericType>()
            for (type in original.genericTypes) {
                if (!genericTypeResolver.map.containsKey(type)) {
                    remainingGenericTypes.add(type)
                }
            }
            val resolvedFunctionType = original.type.resolveGenerics(genericTypeResolver)

            return DelegateForResolvedGenerics(parent, original, resolvedFunctionType, remainingGenericTypes.toList() )
        }

    }

    override fun serialize(writer: CodeWriter) {
        writer.append("delegate typed $type with resolved generics for $wrapped")
    }


    override fun call(
        receiver: Any?,
        children: List<Expression?>,
        parameterScope: LocalRuntimeContext
    ) = (wrapped as Callable).call(receiver, children, parameterScope)

    override fun toString() =
        CodeWriter().apply { serialize(this) }.toString()

}