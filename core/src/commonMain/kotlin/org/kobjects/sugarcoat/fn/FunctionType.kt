package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ResolutionContext
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolver
import org.kobjects.sugarcoat.type.Type

data class FunctionType(
    val returnType: Type,
    val parameterTypes: List<ParameterDefinition>
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

    override fun resolveGenerics(state: GenericTypeResolver, expected: Type?): Type? {
        if (expected != null && expected !is FunctionType) {
            if (parameterTypes.isEmpty()) {
                val resolvedType = returnType.resolveGenerics(state, expected)
                return if (resolvedType == null) null else FunctionType(resolvedType)
            }
        }

        require (expected == null || expected is FunctionType) {
            "${state.errorPrefix()}: Can't resolve function type to expectation: $expected"
        }

        val resolvedReturnType = returnType.resolveGenerics(state, expected?.returnType)
        if (resolvedReturnType == null) {
            return null
        }
        val builder = mutableListOf<ParameterDefinition>()
        for ((i, parameter) in parameterTypes.withIndex()) {
            val resolvedType = parameter.type.resolveGenerics(state, if (expected == null) null else expected.parameterTypes[i].type)
            if (resolvedType == null) {
                return null
            }
            builder.add(parameter.copy(type = resolvedType))
        }
        return FunctionType(returnType, builder.toList())
    }

    override fun matchImpl(other: Type, genericTypeResolver: GenericTypeResolver, lazyMessage: () -> String): Type {
        require(other is FunctionType && parameterTypes.size == other.parameterTypes.size, lazyMessage)

        return FunctionType(
            returnType.match(other.returnType, genericTypeResolver, lazyMessage),
            parameterTypes.mapIndexed { index, param ->
                val otherParam = other.parameterTypes[index]
                require (param.repeated == otherParam.repeated, lazyMessage)
                param.copy(type = param.type.match(otherParam.type, genericTypeResolver, lazyMessage))
            }
        )
    }

}