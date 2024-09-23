package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.model.Instance

class ParameterConsumer(
    val parameterReferences: List<ParameterReference>
) {
    var index = 0
    val consumed = mutableSetOf<Int>()

    fun read(parameterContext: RuntimeContext, parameterDefinition: ParameterDefinition) =
        read(parameterContext, parameterDefinition.name, parameterDefinition.type, parameterDefinition.repeated)

    fun read(parameterContext: RuntimeContext, name: String, type: Type, repeated: Boolean = false): Any {
        if (repeated) {
            val result = mutableListOf<Any>()
            while (true) {
                val p = readSingle(parameterContext, name, type) ?: break
                result.add(p)
            }
            return result.toList()
        }
        return readSingle(parameterContext, name, type) ?: throw IllegalStateException("Parameter $name not found in argument list $parameterReferences")
    }


    private fun readSingle(
        parameterContext: RuntimeContext,
        name: String,
        type: Type
    ): Any? {
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