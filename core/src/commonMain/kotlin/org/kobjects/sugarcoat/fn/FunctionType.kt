package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ResolutionContext
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

data class FunctionType(
    val returnType: Type,
    val parameterTypes: List<ParameterDefinition>,
) : Type {
    constructor(returnType: Type, vararg parameterTypes: ParameterDefinition) : this(returnType, parameterTypes.asList())

    override fun resolveType(context: Classifier): FunctionType {
        val resolvedReturnType = returnType.resolveType(context)
        val resolvedParameterTypes = List(parameterTypes.size) { parameterTypes[it].resolveType(context) }
        return FunctionType(resolvedReturnType, resolvedParameterTypes)
    }

    fun resolveDefaultExpressions(resolutionContext: ResolutionContext): FunctionType {
        val resolvedParameters = List(parameterTypes.size) { parameterTypes[it].resolveDefaultExpression(resolutionContext) }
        return FunctionType(returnType, resolvedParameters)
    }

    override fun resolveGenerics(state: GenericTypeResolver): FunctionType {

        val resolvedReturnType = returnType.resolveGenerics(state)

        val builder = mutableListOf<ParameterDefinition>()
        for (parameter in parameterTypes) {
            val resolvedType = parameter.type.resolveGenerics(state)
            builder.add(parameter.copy(type = resolvedType))
        }
        return FunctionType(resolvedReturnType, builder.toList())
    }

    override fun matchImpl(other: Type, genericTypeResolver: GenericTypeResolver?, lazyMessage: () -> String) {
        require(other is FunctionType && parameterTypes.size == other.parameterTypes.size, lazyMessage)

        returnType.match(other.returnType, genericTypeResolver, lazyMessage)
        for ((index, parameter) in parameterTypes.withIndex()) {
            parameter.restType().match(other.parameterTypes[index].restType(), genericTypeResolver, lazyMessage)

        }
    }

    override fun getGenericTypes(): Set<GenericType> {
        val result = mutableSetOf<GenericType>()
        result.addAll(returnType.getGenericTypes())
        for (p in parameterTypes) {
            result.addAll(p.type.getGenericTypes())
        }
        return result.toSet()
    }

}