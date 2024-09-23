package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.fn.RuntimeContext



class SymbolExpression(
    val namespace: Namespace,
    val receiver: Expression?,
    val name: String,
    val children: List<ParameterReference>,
    val precedence: Int = 0
) : Expression {
    constructor(namespace: Namespace, receiver: Expression, name: String, precedence: Int, vararg children: Expression) : this(namespace, receiver, name, children.map { ParameterReference("", it) }, precedence)
    constructor(namespace: Namespace, name: String, vararg children: Expression) : this(namespace, null, name, children.map { ParameterReference("", it) })

    override fun eval(context: RuntimeContext) = context.evalSymbol(receiver?.eval(context), name, children)

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
                            if (children.isNotEmpty()) {
                                children.first().value.stringify(stringBuilder, precedence)
                            }
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

    override fun getType(): Type = throw UnsupportedOperationException()
}