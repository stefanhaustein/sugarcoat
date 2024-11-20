package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.type.Type

class ResolutionContext(
    val namespace: Classifier,
    val parent: ResolutionContext? = null,
) {
    private val locals: MutableMap<String, Variable> = parent?.locals ?: mutableMapOf()

    fun addLocal(name: String, type: Type, mutable: Boolean) {
        require(!locals.contains(name)) {
            "Local variable $name already defined."
        }

        locals[name] = Variable(name, type, mutable)
    }

    fun resolveOrNull(name: String): Callable? {
        val variable = locals[name]
        if (variable != null) {
            return LocalGetter(variable)
        }
        if (name.startsWith("set_")) {
            val setVariable = locals[name.substring(4)]
            if (setVariable != null) {
                return LocalSetter(setVariable)
            }
        }
        return null
    }

    data class Variable(
        val name: String,
        val type: Type,
        val mutable: Boolean = false
    )

    data class LocalGetter(val variable: Variable) : Callable {
        override val type: FunctionType
            get() = FunctionType(variable.type)

        override val static: Boolean
            get() = true

        override fun call(
            receiver: Any?,
            children: List<Expression?>,
            parameterScope: LocalRuntimeContext
        ): Any {
            return if (variable.name == "self")  parameterScope.instance!!
            else parameterScope.symbols[variable.name]!!
        }
    }

    data class LocalSetter(val variable: Variable) : Callable {
        override val type: FunctionType
            get() = FunctionType(VoidType, ParameterDefinition("value", variable.type))

        override val static: Boolean
            get() = true

        override fun call(
            receiver: Any?,
            children: List<Expression?>,
            parameterScope: LocalRuntimeContext
        ): Any {
            parameterScope.symbols[variable.name] = children[0]!!.eval(parameterScope)
            return Unit
        }
    }


}