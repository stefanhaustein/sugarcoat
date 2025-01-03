package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.fn.AbstractFunctionDefinition

import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.model.Namespace

data class NativeFunction(
    override val parent: Namespace,
    override val static: Boolean,
    val returnType: Type,
    override val name: String,
    val args: List<ParameterDefinition>,
    val op: (NativeArgList) -> Any
) : AbstractFunctionDefinition(parent, name) {


    override fun call(
        receiver: Any?,
        children: List<Expression?>,
        parameterScope: LocalRuntimeContext
    ): Any {
        require(static == (receiver == null)) {
            if (static) "Unexpected receiver for static method $this." else "Receiver expected for instance method $this."
        }
        val evaluated = if (static) mutableListOf<Any>() else mutableListOf<Any>(receiver!!)
        for (child in children) {
            evaluated.add(child!!.eval(parameterScope))
        }
        return op(NativeArgList(evaluated))
    }

    override val type: FunctionType
        get() = FunctionType(returnType, args)

    override fun serialize(writer: CodeWriter) {
        writer.append("native fn $name\n")
    }
}