package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.ast.Callable
import org.kobjects.sugarcoat.ast.Definition
import org.kobjects.sugarcoat.ast.ParameterConsumer
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.Type
import org.kobjects.sugarcoat.runtime.RuntimeContext

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