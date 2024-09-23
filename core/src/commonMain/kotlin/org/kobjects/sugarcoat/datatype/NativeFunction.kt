package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.fn.ParameterConsumer
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.base.Scope
import org.kobjects.sugarcoat.model.Instance

class NativeFunction(
    override val parent: NativeType,
    val static: Boolean,
    val returnType: Type,
    override val name: String,
    val args: Array<out Pair<String, Type>>,
    val op: (List<Scope>) -> Scope
) : Namespace, Callable {


    override fun call(
        receiver: Instance?,
        children: List<ParameterReference>,
        parameterScope: Scope
    ): Scope {
        require(static == (receiver == null)) {
            if (static) "Unexpected receiver for static method." else "Receiver expected for instance method."
        }
        val parameterConsumer = ParameterConsumer(children)
        val parameterList: MutableList<Scope> =
            if (static) mutableListOf() else mutableListOf(receiver!!)
        for (parameter in args) {
            parameterList.add(parameterConsumer.read(parameterScope, parameter.first, parameter.second))
        }
        return op(parameterList)
    }
}