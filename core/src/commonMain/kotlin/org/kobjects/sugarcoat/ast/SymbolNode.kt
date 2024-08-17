package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RuntimeContext


class SymbolNode(
    val receiver: Node?,
    val name: String,
    val children: List<ParameterReference>,
    val precedence: Int = 0
) : Node {
    constructor(receiver: Node?, name: String, precedence: Int, vararg children: Node) : this(receiver, name, children.map { ParameterReference("", it) }, precedence)
    constructor(name: String, vararg children: Node) : this(null, name, children.map { ParameterReference("", it) })
    override fun eval(context: RuntimeContext): RuntimeContext = if (receiver == null) context.evalSymbol(name, children, context)
        else {
            val baseContext = receiver.eval(context)
            baseContext.evalSymbol(name, children, context)
        }

    override fun toString(): String = buildString { stringify(this, 0) }

    override fun stringify(stringBuilder: StringBuilder, parentPrecedence: Int) {
        if (name.firstOrNull { !it.isLetterOrDigit() } != null) {
            if (parentPrecedence > 0 && parentPrecedence >= precedence) {
                stringBuilder.append("(")
                stringBuilder.append(this)
                stringBuilder.append(")")
            } else {
                when (children.size) {
                    0 -> {
                        if (receiver == null) {
                            stringBuilder.append("'$name'")
                        } else {
                            stringBuilder.append(name)
                            children.first().value.stringify(stringBuilder, precedence)
                        }
                    }
                    else -> {
                        receiver?.stringify(stringBuilder, precedence)
                        for (child in children) {
                            stringBuilder.append(" $name ")
                            child.value.stringify(stringBuilder, precedence)
                        }
                    }
                }
            }
        } else {
            if (receiver != null) {
                receiver.stringify(stringBuilder, 0)
                stringBuilder.append(".")
            }
            stringBuilder.append(name)
            if (children.isNotEmpty()) {
                stringBuilder.append(children.joinToString(", ", "(", ")"))
            }
        }

    }
}