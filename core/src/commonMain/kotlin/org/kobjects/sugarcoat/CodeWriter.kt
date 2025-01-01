package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.fn.Lambda

class CodeWriter {
    val sb = StringBuilder()
    var indent = ""

    fun append(value: Any): CodeWriter {
        sb.append(value)
        return this
    }

    fun newline() {
        sb.append('\n').append(indent)
    }

    fun indent() {
        indent += "  "
    }

    fun outdent() {
        indent = indent.dropLast(2)
    }

    fun writeInvocation(receiver: Expression?, name: String, parameters: List<Pair<String, Expression?>>) {
        if (receiver != null) {

            // Operator?

            if (name.any { !it.isLetterOrDigit() } && parameters.size <= 1) {
                if (parameters.isEmpty()) {
                    append(name)
                    receiver.serialize(this)
                } else {
                    receiver.serialize(this)
                    append(" $name ")
                    parameters.first().second!!.serialize(this)
                }
                return
            }

            receiver.serialize(this)
            append(".")
        }
        append(name)

        if (parameters.isEmpty()) {
            return
        }


        append("(")
        //   writer.indent()

        var index = 0
        var skipped = false
        var first = true

        for ((paramName, paramExpr) in parameters) {
            when (paramExpr) {
                null -> {
                    skipped = true
                    continue
                }
                is LiteralExpression -> if (paramExpr.value is Lambda) break
            }
            index++
            if (first) {
                first = false
            } else {
                append(", ")
            }
            if (skipped) {
                append("$paramName = ")
            }
            paramExpr.serialize(this)
        }
        append(")")

        while (index < parameters.size) {
            var (paramName, paramExpr) = parameters[index]
            if (paramExpr != null) {
                newline()
                append("--$paramName")
                indent()
                newline()
                paramExpr.serialize(this)
                outdent()
            }
            index++
        }

        // writer.outdent()
    }



    override fun toString() = sb.toString()
}