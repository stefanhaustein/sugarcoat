package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.base.ImplicitType
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.model.Classifier


class SymbolExpression(
    val namespace: Classifier,
    val receiver: Expression?,
    val name: String,
    val children: List<ParameterReference>,
    val precedence: Int = 0
) : Expression {
    constructor(namespace: Classifier, receiver: Expression, name: String, precedence: Int, vararg children: Expression) : this(namespace, receiver, name, children.map { ParameterReference("", it) }, precedence)
    constructor(namespace: Classifier, name: String, vararg children: Expression) : this(namespace, null, name, children.map { ParameterReference("", it) })

    override fun eval(context: LocalRuntimeContext) = context.evalSymbol(namespace, receiver?.eval(context), name, children)

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

    override fun getType(): Type =
        ImplicitType {
            val receiverNamespace = if (receiver == null) namespace else receiver.getType().resolve() as Classifier
            val rawType = receiverNamespace.resolve(name)
            (if (rawType is Callable) (Type.of(rawType) as FunctionType).returnType else rawType as Type).resolve()
    }
}