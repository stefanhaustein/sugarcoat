package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RootContext
import org.kobjects.sugarcoat.runtime.RuntimeContext

abstract class NativeClassifierDefinition<T>(name: String): AbstractClassifierDefinition(null, name) {

    fun addNativeMethod(
        name: String,
        vararg parameter: ParameterDefinition,
        implementation: (receiver: T, parameter: List<RuntimeContext>) -> RuntimeContext
    ) {
        val impl = NativeFunction(name, parameter, implementation)
        addDefinition(impl.name, impl)
    }


    inner class NativeFunction(
        val name: String,
        val paramter: Array<out ParameterDefinition>,
        val implementation: (receiver: T, parameter: List<RuntimeContext>) -> RuntimeContext
    ) : Callable, Definition {
        override fun call(
            receiver: RuntimeContext,
            children: List<ParameterReference>,
            parameterContext: RuntimeContext
        ): RuntimeContext {
            val parameterConsumer = ParameterConsumer(children)
            val value = (receiver as NativeClassifierDefinition<T>.NativeInstance).value
            val parameterList = mutableListOf<RuntimeContext>()
            for (p in paramter) {
                parameterList.add(parameterConsumer.read(parameterContext, p))
            }
            parameterConsumer.done()
            return implementation.invoke(value, parameterList)
        }

        override val parent: Definition?
            get() = this@NativeClassifierDefinition

        override fun addDefinition(name: String, value: Definition) {
            throw UnsupportedOperationException()
        }
    }

    inner class NativeInstance(val value: T): RuntimeContext {
        override fun evalSymbol(
            name: String,
            children: List<ParameterReference>,
            parameterContext: RuntimeContext
        ): RuntimeContext =
            (this@NativeClassifierDefinition.definitions[name] as Callable).call(this, children, parameterContext)

    }

}