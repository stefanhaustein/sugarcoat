package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.runtime.RuntimeContext

class ParameterConsumer(
    val parameterReferences: List<ParameterReference>
) {
    var index = 0
    val consumed = mutableSetOf<Int>()

    fun read(parameterDefinition: ParameterDefinition, parameterContext: RuntimeContext): RuntimeContext {
        if (parameterDefinition.repeated) {
            val result = mutableListOf<RuntimeContext>()
            while (true) {
                val p = readSingle(parameterDefinition, parameterContext)
                if (p == null) {
                    break
                }
                result.add(p)
            }
            return ListType.Instance(result.toList())
        }
        return readSingle(parameterDefinition, parameterContext) ?: throw IllegalStateException("Parameter $parameterDefinition not found in argument list $parameterReferences")
    }


    fun readSingle(parameterDefinition: ParameterDefinition, parameterContext: RuntimeContext): RuntimeContext? {
        var rawResult: Expression? = null
        if (index < parameterReferences.size
            && parameterReferences[index].name.isEmpty()) {
            consumed.add(index)
            rawResult = parameterReferences[index++].value
        } else {
            for (i in parameterReferences.indices) {
                if (!consumed.contains(i) && parameterReferences[i].name == parameterDefinition.name) {
                    rawResult = parameterReferences[i].value
                    break
                }
            }
        }
        return if (rawResult == null) null
            else if (parameterDefinition.type is FunctionType) rawResult.eval(parameterContext)
            else Closure(rawResult, parameterContext)
    }
}