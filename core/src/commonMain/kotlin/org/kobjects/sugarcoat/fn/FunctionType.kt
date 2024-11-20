package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.type.GenericType
import org.kobjects.sugarcoat.type.GenericTypeResolverState
import org.kobjects.sugarcoat.type.Type

data class FunctionType(
    val returnType: Type,
    val parameterTypes: List<ParameterDefinition>
) : Type {
    constructor(returnType: Type, vararg parameterTypes: ParameterDefinition) : this(returnType, parameterTypes.asList())

    override fun resolve(context: Classifier): Type {
        val resolvedReturnType = returnType.resolve(context)
        val resolvedParameterTypes = List(parameterTypes.size) { parameterTypes[it].resolveType(context) }
        return FunctionType(resolvedReturnType, resolvedParameterTypes)
    }
    
    override fun resolveGenerics(state: GenericTypeResolverState, expected: Type?): Type? {
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

    override fun assignableFrom(other: Type): Boolean {
        if (other !is FunctionType)  {
            return false
        }
        if (parameterTypes.size != other.parameterTypes.size) {
            return false
        }
        if (!returnType.assignableFrom(other.returnType)) {
            return false
        }
        for ((index, parameter) in parameterTypes.withIndex()) {
            if (!parameter.type.assignableFrom(other.parameterTypes[index].type)) {
                return false
            }
        }
        return true
    }

}