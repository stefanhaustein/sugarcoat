package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.fn.TypedCallable
import org.kobjects.sugarcoat.parser.Position

class ParameterConsumer(
    val position: Position,
    val parameterReferences: List<ParameterReference>
) {
    var index = 0
    val consumed = mutableSetOf<Int>()

    fun read(parameterDefinition: ParameterDefinition) =
        read(parameterDefinition.name, parameterDefinition.repeated, parameterDefinition.defaultValue != null)

    fun read(name: String, repeated: Boolean = false, optional: Boolean): Expression? {
        if (repeated) {
            val result = mutableListOf<Expression>()
            while (true) {
                val p = readSingle(name) ?: break
                result.add(p)
            }
            return ListExpression(position, result.toList())
        }
        return readSingle(name) ?: if (optional) null else throw IllegalStateException("$position: Required parameter '$name' not found in argument list $parameterReferences")
    }


    private fun readSingle(
        name: String,
    ): Expression? {
        if (index < parameterReferences.size
            && parameterReferences[index].name.isEmpty()) {
            consumed.add(index)
            return parameterReferences[index++].value
        }
        for (i in parameterReferences.indices) {
            if (!consumed.contains(i) && parameterReferences[i].name == name) {
                consumed.add(i)
                return parameterReferences[i].value
            }
        }
        return null
    }

    fun done(target: TypedCallable) {
        require(consumed.size == parameterReferences.size) {
            "Only ${consumed.size} $consumed of ${parameterReferences.size} parameters consumed from $parameterReferences for $target"
        }
    }
}