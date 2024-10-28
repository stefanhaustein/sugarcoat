package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.type.MetaType
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.TypedCallable
import org.kobjects.sugarcoat.model.Classifier


class UnresolvedSymbolExpression(
    val namespace: Classifier,
    val receiver: Expression?,
    val name: String,
    val children: List<ParameterReference>,
    val precedence: Int = 0
) : Expression {
    constructor(namespace: Classifier, receiver: Expression, name: String, precedence: Int, vararg children: Expression) : this(namespace, receiver, name, children.map { ParameterReference("", it) }, precedence)
    constructor(namespace: Classifier, name: String, vararg children: Expression) : this(namespace, null, name, children.map { ParameterReference("", it) })

    override fun eval(context: LocalRuntimeContext) = throw UnsupportedOperationException()

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

    override fun getType() = throw UnsupportedOperationException()

    fun resolveChildren(callable: TypedCallable): List<Expression?> {
        val parameterConsumer = ParameterConsumer(children)
        val resolved = mutableListOf<Expression?>()
        for (param in callable.type.parameterTypes) {
            val expr = parameterConsumer.read(param)
            resolved.add(expr?.resolve(param.type))
        }
        parameterConsumer.done(callable)

        return resolved.toList()
    }

    override fun resolve(expectedType: Type?): Expression {
        if (receiver == null) {
            val self = namespace.resolveOrNull("self")
            val resolvedDynamically = if (self == null) null else
                ((self as TypedCallable).type.returnType as Classifier).resolveOrNull(name)

            if (resolvedDynamically is TypedCallable) {
                return CallExpression(
                    if (resolvedDynamically.static) null else CallExpression(null, self as TypedCallable, emptyList()),
                    resolvedDynamically,
                    resolveChildren(resolvedDynamically))
            }

            return resolveStatically(null, namespace.resolve(name))
        }

        val resolvedReceiver = receiver.resolve(null)
        val type = resolvedReceiver.getType()
        return when (type) {
            is MetaType -> {
                val resolved = type.type.resolve(name)
                resolveStatically(null, resolved)
            }
            is Classifier -> {
                val resolved = type.resolve(name)
                resolveStatically(resolvedReceiver, resolved)
            }
            else -> throw IllegalStateException(
                "Type '$type' (${type::class}) of resolved receiver '$resolvedReceiver' must be classifier for resolving '$name'")
        }
    }

    fun resolveStatically(resolvedReceiver: Expression?, resolved: Classifier): Expression {
        if (resolved is TypedCallable) {
            return CallExpression(resolvedReceiver, resolved, resolveChildren(resolved))
        }
        if (resolved is Type) {
            require(children.isEmpty()) {
                "Types can't have function parameters."
            }
            return LiteralExpression(resolved)
        }
        throw IllegalStateException("Unrecognized resolved name: '$resolved'")
    }
}