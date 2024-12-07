package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.Lambda
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.parser.Position

data class CallExpression(
    override val position: Position,
    val receiver: Expression?,
    val fn: Callable,
    val parameter: List<Expression?>
): ResolvedExpression(position) {
    override fun eval(context: LocalRuntimeContext): Any =
        fn.call(receiver?.eval(context), parameter, context)

    override fun getType() = fn.type.returnType


    override fun serialize(writer: CodeWriter) {
        if (receiver != null) {
            receiver.serialize(writer)
            writer.append(".")
        }
        if (fn is Classifier) {
            writer.append(fn.name)
        } else {
            writer.append(fn.toString())
        }
        writer.append("(")
     //   writer.indent()

        var index = 0
        var skipped = false
        var first = true

        for (p in parameter) {
            when (p) {
                null -> {
                    skipped = true
                    continue
                }
                is LiteralExpression -> if (p.value is Lambda) break
            }
            index++
            if (first) {
                first = false
            } else {
                writer.append(", ")
            }
            if (skipped) {
                writer.append("${fn.type.parameterTypes[index].name} = ")
            }
            p!!.serialize(writer)
        }
        writer.append(")")

        while (index < parameter.size) {
            var p = parameter[index]
            if (p != null) {
                writer.newline()
                writer.append("--${fn.type.parameterTypes[index].name}")
                writer.indent()
                writer.newline()
                p.serialize(writer)
                writer.outdent()
            }
            index++
        }

        // writer.outdent()
    }

}