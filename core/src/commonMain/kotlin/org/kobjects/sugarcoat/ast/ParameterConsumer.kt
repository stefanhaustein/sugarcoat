package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.fn.TypedCallable

class ParameterConsumer(
    val parameterReferences: List<ParameterReference>
) {
    var index = 0
    val consumed = mutableSetOf<Int>()

    fun read(parameterDefinition: ParameterDefinition) =
        read(parameterDefinition.name, parameterDefinition.repeated)

    fun read(name: String, repeated: Boolean = false): Expression? {
        if (repeated) {
            val result = mutableListOf<Expression>()
            while (true) {
                val p = readSingle(name) ?: break
                result.add(p)
            }
            return ListExpression(result.toList())
        }
        return readSingle(name) ?: throw IllegalStateException("Parameter $name not found in argument list $parameterReferences")
    }


    private fun readSingle(
        name: String,
    ): Expression? {
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
        return rawResult
    }

    fun done(target: TypedCallable) {
        require(consumed.size == parameterReferences.size) {
            "Only ${consumed.size} $consumed of ${parameterReferences.size} parameters consumed from $parameterReferences for $target"
        }
    }
}