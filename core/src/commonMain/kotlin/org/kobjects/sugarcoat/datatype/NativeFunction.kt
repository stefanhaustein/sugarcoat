package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.Callable

import org.kobjects.sugarcoat.fn.ParameterConsumer
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.base.Typed
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.model.Classifier

data class NativeFunction(
    override val parent: Classifier,
    override val static: Boolean,
    val returnType: Type,
    override val name: String,
    val args: Array<out Pair<String, Type>>,
    val op: (NativeArgList) -> Any
) : Classifier(parent, name), Callable, Typed {


    override fun call(
        receiver: Any?,
        children: List<ParameterReference>,
        parameterScope: LocalRuntimeContext
    ): Any {
        require(static == (receiver == null)) {
            if (static) "Unexpected receiver for static method $this." else "Receiver expected for instance method $this."
        }
        val parameterConsumer = ParameterConsumer(children)
        val parameterList: MutableList<Any> =
            if (static) mutableListOf() else mutableListOf(receiver!!)
        for (parameter in args) {
            parameterList.add(parameterConsumer.read(parameterScope, parameter.first, parameter.second))
        }
        return op(NativeArgList(parameterList))
    }

    override val type: Type
        get() = FunctionType(args.map { it.second }, returnType)

    override fun serialize(sb: StringBuilder) {
        sb.append("native fn $name\n")
    }
}