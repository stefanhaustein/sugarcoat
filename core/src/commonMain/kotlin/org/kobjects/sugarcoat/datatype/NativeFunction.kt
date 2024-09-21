package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.base.Definition
import org.kobjects.sugarcoat.fn.ParameterConsumer
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.base.RuntimeContext
import org.kobjects.sugarcoat.model.Instance

class NativeFunction(
    override val parent: NativeType,
    val static: Boolean,
    val returnType: Type,
    override val name: String,
    val args: Array<out Pair<String, Type>>,
    val op: (List<RuntimeContext>) -> RuntimeContext
) : Definition, Callable {


    override fun call(
        receiver: Instance?,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        require(static == (receiver == null)) {
            if (static) "Unexpected receiver for static method." else "Receiver expected for instance method."
        }
        val parameterConsumer = ParameterConsumer(children)
        val parameterList: MutableList<RuntimeContext> =
            if (static) mutableListOf() else mutableListOf(receiver!!)
        for (parameter in args) {
            parameterList.add(parameterConsumer.read(parameterContext, parameter.first, parameter.second))
        }
        return op(parameterList)
    }
}