package org.kobjects.sugarcoat


class Symbol(
    val receiver: Evaluable?,
    val name: String,
    val children: List<Parameter>,
    val precedence: Int = 0
) : Evaluable {
    constructor(receiver: Evaluable?, name: String, precedence: Int, vararg children: Evaluable) : this(receiver, name, children.map { Parameter("", it) }, precedence)
    constructor(name: String, method: Boolean, vararg children: Evaluable) : this(null, name, children.map { Parameter("", it) })
    override fun eval(context: RuntimeContext): Any = if (receiver == null) context.evalSymbol(name, children, context)
        else {
            val base = receiver.eval(context)
            val baseContext = when (base)  {
                is RuntimeContext -> base
                is Boolean -> BooleanContext(base)
                is Double -> DoubleContext(base)
                else -> throw UnsupportedOperationException("Value $base can't be used as context for method invocations.")
            }
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