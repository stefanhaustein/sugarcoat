package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ResolutionContext
import org.kobjects.sugarcoat.model.Namespace
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

data class FunctionType(
    val receiverType: Type?,
    val parameterTypes: List<ParameterDefinition>,
    val returnType: Type,
) : Type {
    constructor(receiverType: Type?, returnType: Type, vararg parameterTypes: ParameterDefinition) : this(
        receiverType,
        parameterTypes.asList(),
        returnType
    )

    val static: Boolean
        get() = receiverType == null

    override fun resolveType(context: Namespace): FunctionType {
        val resolvedReceiverType = receiverType?.resolveType(context)
        val resolvedReturnType = returnType.resolveType(context)
        val resolvedParameterTypes = List(parameterTypes.size) { parameterTypes[it].resolveType(context) }
        return FunctionType(resolvedReceiverType, resolvedParameterTypes, resolvedReturnType)
    }

    fun resolveDefaultExpressions(resolutionContext: ResolutionContext): FunctionType {
        val resolvedParameters = List(parameterTypes.size) { parameterTypes[it].resolveDefaultExpression(resolutionContext) }
        return FunctionType(receiverType, resolvedParameters, returnType)
    }

    override fun resolveGenerics(state: GenericTypeResolver): FunctionType {

        val resolvedReceiverType = receiverType?.resolveGenerics(state)
        val resolvedReturnType = returnType.resolveGenerics(state)

        val builder = mutableListOf<ParameterDefinition>()
        for (parameter in parameterTypes) {
            val resolvedType = parameter.type.resolveGenerics(state)
            builder.add(parameter.copy(type = resolvedType))
        }
        return FunctionType(resolvedReceiverType, builder.toList(), resolvedReturnType)
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