package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.Lambda
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.GenericTypeResolver
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

    fun resolveOrNull(pos: Position, name: String): Callable? {
        val variable = locals[name]
        if (variable != null) {
            return LocalGetter(pos, variable)
        }
        if (name.startsWith("set_")) {
            val setVariable = locals[name.substring(4)]
            if (setVariable != null) {
                return LocalSetter(pos, setVariable)
            }
        }
        return null
    }
    

    data class Variable(
        val name: String,
        val type: Type,
        val mutable: Boolean = false
    )

    data class LocalGetter(val pos: Position, val variable: Variable) : Callable {
        override val type: FunctionType
            get() = FunctionType(variable.type)

        override val static: Boolean
            get() = true

        override fun call(
            receiver: Any?,
            children: List<Expression?>,
            parameterScope: LocalRuntimeContext
        ): Any {
            return if (variable.name == "self")  parameterScope.instance ?: throw IllegalStateException("$pos: self can't be resolved in $parameterScope")
            else parameterScope.get(variable.name, pos)
        }

        override fun toString() = variable.name
    }

    data class LocalSetter(val pos: Position, val variable: Variable) : Callable {
        override val type: FunctionType
            get() = FunctionType(VoidType, ParameterDefinition("value", variable.type))

        override val static: Boolean
            get() = true

        override fun call(
            receiver: Any?,
            children: List<Expression?>,
            parameterScope: LocalRuntimeContext
        ): Any {
            parameterScope.set(variable.name, children[0]!!.eval(parameterScope))
            return Unit
        }
    }


}