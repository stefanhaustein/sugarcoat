package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.type.MetaType
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.TypedCallable
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.GenericTypeResolverState


class UnresolvedSymbolExpression(
    position: Position,
    val receiver: Expression?,
    val name: String,
    val children: List<ParameterReference>,
    val precedence: Int = 0
) : Expression(position) {
    constructor(position: Position, receiver: Expression, name: String, precedence: Int, vararg children: Expression) : this(position, receiver, name, children.map { ParameterReference("", it) }, precedence)
    constructor(position: Position, name: String, vararg children: Expression) : this(position, null, name, children.map { ParameterReference("", it) })

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

    fun arrangeChildren(callable: TypedCallable): List<Expression?> {
        val parameterConsumer = ParameterConsumer(children)
        val builder = mutableListOf<Expression?>()
        for (param in callable.type.parameterTypes) {
            builder.add(parameterConsumer.read(param))
        }
        parameterConsumer.done(callable)

        return builder.toList()
    }

    override fun resolve(context: ResolutionContext, expectedType: Type?): Expression {
        if (receiver == null) {
            val local = context.resolveOrNull(name)
            if (local != null) {
                return buildCallExpression(context,null, local, expectedType)
            }

            val self = context.resolveOrNull("self")
            val resolvedDynamically = if (self == null) null else
                (self.type.returnType as Classifier).resolveSymbolOrNull(name)

            if (resolvedDynamically is TypedCallable) {
                return buildCallExpression(
                    context,
                    if (resolvedDynamically.static) null else CallExpression(position, null, self as TypedCallable, emptyList()),
                    resolvedDynamically,
                    expectedType)
            }

            return resolveStatically(context,null, context.namespace.resolveSymbol(name), expectedType)
        }

        val resolvedReceiver = receiver.resolve(context, null)
        val type = resolvedReceiver.getType()
        return when (type) {
            is MetaType -> {
                val resolved = type.type.resolveSymbol(name)
                resolveStatically(context, null, resolved, expectedType)
            }
            is Classifier -> {
                val resolved = type.resolveSymbol(name)
                resolveStatically(context, resolvedReceiver, resolved, expectedType)
            }
            else -> throw IllegalStateException(
                "$position: Type '$type' (${type::class}) of resolved receiver '$resolvedReceiver' must be classifier for resolving '$name'")
        }
    }

    fun resolveStatically(context: ResolutionContext, resolvedReceiver: Expression?, resolvedMethod: Classifier, expectedType: Type?): Expression {
        if (resolvedMethod is TypedCallable) {
            return buildCallExpression(context, resolvedReceiver, resolvedMethod, expectedType)
        }
        if (resolvedMethod is Type) {
            require(children.isEmpty()) {
                "$position: Types can't have function parameters; got $children"
            }
            return LiteralExpression(resolvedMethod)
        }
        throw IllegalStateException("Unrecognized resolved name: '$resolvedMethod'")
    }

    fun buildCallExpression(context: ResolutionContext, resolvedReceiver: Expression?, resolvedMethod: TypedCallable, expectedType: Type?): CallExpression {
        val arrangedChildren = arrangeChildren(resolvedMethod)
        val resolvedChildren = mutableListOf<Expression?>()

        val genericTypeResolverState = GenericTypeResolverState {
            "$position: Resolving $name"
        }
        resolvedMethod.type.returnType.resolveGenerics(genericTypeResolverState, expectedType)

        for ((i, parameter) in resolvedMethod.type.parameterTypes.withIndex()) {
            println("state: $genericTypeResolverState")
            if (genericTypeResolverState.map.isNotEmpty()) {
                println("boo")
            }
            val parameterRestType = parameter.restType()
            val expectedParameterType = parameterRestType.resolveGenerics(genericTypeResolverState)
            val resolvedChild = arrangedChildren[i]?.resolve(context, expectedParameterType)
            if (resolvedChild != null) {
                parameter.restType().resolveGenerics(genericTypeResolverState, resolvedChild.getType())
            }
            resolvedChildren.add(resolvedChild)
        }

        val resolvedReturnType = resolvedMethod.type.returnType.resolveGenerics(genericTypeResolverState, expectedType)
        return CallExpression(position, resolvedReceiver, resolvedMethod, resolvedChildren)
    }

}