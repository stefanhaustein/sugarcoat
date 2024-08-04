package org.kobjects.sugarcoat


class Symbol(
    val name: String,
    val method: Boolean,
    val children: List<Parameter>,
    val precedence: Int = 0
) : Evaluable {
    constructor(name: String, precedence: Int, vararg children: Evaluable) : this(name, true, children.map { Parameter("", it) }, precedence)
    constructor(name: String, method: Boolean, vararg children: Evaluable) : this(name, method, children.map { Parameter("", it) })
    override fun eval(context: RuntimeContext): Any = if (method) {
        val base = children[0].value.eval(context)
        val baseContext = when (base)  {
            is RuntimeContext -> base
            is Boolean -> BooleanContext(base)
            is Double -> DoubleContext(base)
            else -> throw UnsupportedOperationException("Value $base can't be used as context for method invocations.")
        }
        baseContext.evalMethod(name, children, context)
    } else context.evalSymbol(name, children, context)

    override fun toString(): String = buildString { stringify(this, 0) }

    override fun stringify(stringBuilder: StringBuilder, parentPrecedence: Int) {
        if (name.firstOrNull { !it.isLetterOrDigit() } != null) {
            if (parentPrecedence > 0 && parentPrecedence >= precedence) {
                stringBuilder.append("(")
                stringBuilder.append(this)
                stringBuilder.append(")")
            } else {
                when (children.size) {
                    0 -> stringBuilder.append("'$name'")
                    1 -> {
                        stringBuilder.append(name)
                        children.first().value.stringify(stringBuilder, precedence)
                    }
                    else -> {
                        children.first().value.stringify(stringBuilder, precedence)
                        for (child in children.subList(1, children.size)) {
                            stringBuilder.append(" $name ")
                            child.value.stringify(stringBuilder, precedence)
                        }
                    }
                }
            }
        } else {
            stringBuilder.append(name)
            if (children.isNotEmpty()) {
                stringBuilder.append(children.joinToString(", ", "(", ")"))
            }
        }

    }
}