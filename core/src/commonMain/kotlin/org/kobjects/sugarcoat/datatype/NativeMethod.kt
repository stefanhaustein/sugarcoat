package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.base.Definition
import org.kobjects.sugarcoat.fn.ParameterConsumer
import org.kobjects.sugarcoat.fn.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.base.RuntimeContext

class NativeMethod(
    override val parent: NativeType,
    val returnType: Type,
    val name: String,
    val args: Array<out Pair<String, Type>>,
    val op: (List<RuntimeContext>) -> RuntimeContext
) : Definition, Callable {

    override fun addDefinition(name: String, value: Definition) {
        throw UnsupportedOperationException()
    }

    override fun call(
        receiver: RuntimeContext,
        children: List<ParameterReference>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        val parameterConsumer = ParameterConsumer(children)
        val parameterList = mutableListOf(receiver)
        for (parameter in args) {
            parameterList.add(parameterConsumer.read(parameterContext, parameter.first, parameter.second))
        }
        return op(parameterList)
    }
}