package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.runtime.RuntimeContext

class ParameterConsumer(
    val parameterReferences: List<ParameterReference>
) {
    var index = 0
    val consumed = mutableSetOf<Int>()

    fun read(parameterContext: RuntimeContext, parameterDefinition: ParameterDefinition) = read(parameterContext, parameterDefinition.name, parameterDefinition.type, parameterDefinition.repeated)

    fun read(parameterContext: RuntimeContext, name: String, type: Type, repeated: Boolean = false): RuntimeContext {
        if (repeated) {
            val result = mutableListOf<RuntimeContext>()
            while (true) {
                val p = readSingle(parameterContext, name, type)
                if (p == null) {
                    break
                }
                result.add(p)
            }
            return ListType.Instance(result.toList())
        }
        return readSingle(parameterContext, name, type) ?: throw IllegalStateException("Parameter $name not found in argument list $parameterReferences")
    }


    private fun readSingle(
        parameterContext: RuntimeContext,
        name: String,
        type: Type
    ): RuntimeContext? {
        var rawResult: Expression? = null
        if (index < parameterReferences.size
            && parameterReferences[index].name.isEmpty()) {
            consumed.add(index)
            rawResult = parameterReferences[index++].value
        } else {
            for (i in parameterReferences.indices) {
                if (!consumed.contains(i) && parameterReferences[i].name == name) {
                    rawResult = parameterReferences[i].value
                    break
                }
            }
        }
        return if (rawResult == null) null
            else if (type is FunctionType) Closure(rawResult, parameterContext)
            else rawResult.eval(parameterContext)
    }

    fun done() {
        require(consumed.size == parameterReferences.size) {
            "Not all ${parameterReferences.size} parameters consumed: $consumed"
        }
    }
}