package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.Lambda
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
    val parens: Boolean,
    val children: List<ParameterReference>,
) : Expression(position) {

    constructor(position: Position, receiver: Expression, name: String, vararg children: Expression) : this(position, receiver, name, children.size > 0, children.map { ParameterReference("", it) })

    constructor(position: Position, name: String, vararg children: Expression) : this(position, null, name, children.size > 0, children.map { ParameterReference("", it) })

    override fun eval(context: LocalRuntimeContext) = throw UnsupportedOperationException()

    override fun toString(): String = buildString { stringify(this) }

    override fun stringify(stringBuilder: StringBuilder) {
        if (receiver != null) {
            receiver.stringify(stringBuilder)
            stringBuilder.append(".")
        }

        if (name.isEmpty() || !name.first().isLetter()) {
            stringBuilder.append("`$name`")
        } else {
            stringBuilder.append(name)
        }

        if (parens || children.isNotEmpty()) {
            stringBuilder.append('(')
            for ((index, child) in children.withIndex()) {
                if (index > 0) {
                    stringBuilder.append(", ")
                }
                if (child.name.isNotEmpty()) {
                    stringBuilder.append(name)
                    stringBuilder.append(" = ")
                }
                child.value.stringify(stringBuilder)
            }
            stringBuilder.append(')')
        }
    }

    override fun getType() = throw UnsupportedOperationException()

    fun arrangeChildren(callable: TypedCallable): List<Expression?> {
        val parameterConsumer = ParameterConsumer(position.copy(description = "$callable"), children)
        val builder = mutableListOf<Expression?>()
        for (param in callable.type.parameterTypes) {
            builder.add(parameterConsumer.read(param))
        }
        parameterConsumer.done(callable)

        return builder.toList()
    }

    fun buildMethodCall(
        context: ResolutionContext,
        resolvedReceiver: Expression,
        resolvedMember: Classifier,
        expectedType: Type?
    ): CallExpression {
        require(resolvedMember is TypedCallable) {
            "$position: Resolved member is not callable: $resolvedMember"
        }

        require(!resolvedMember.static) {
            "$position: Receiver provided for static call"
        }

        return buildCallExpression(
            context,
            resolvedReceiver,
            resolvedMember,
            expectedType
        )
    }


    override fun resolve(context: ResolutionContext, expectedType: Type?): Expression {
        if (expectedType is FunctionType && expectedType.parameterTypes.isEmpty()) {
            val result = resolveImpl(context, expectedType.returnType)
            return LiteralExpression(position, Lambda(FunctionType(result.getType(), emptyList()), emptyList(), result))
        }
        return resolveImpl(context, expectedType)
    }


    fun resolveImpl(context: ResolutionContext, expectedType: Type?): Expression {
        if (receiver == null) {
            val localVariable = context.resolveOrNull(name)
            if (localVariable != null) {
                return buildCallExpression(context,null, localVariable, expectedType)
            }

            val self = context.resolveOrNull("self")
            
            val resolvedMember = if (self == null) null else
                (self.type.returnType as Classifier).resolveSymbolOrNull(name)

            if (resolvedMember is TypedCallable && !resolvedMember.static) {
                val selfExpression = CallExpression(position, null, self as TypedCallable, emptyList())
                return buildMethodCall(context, selfExpression, resolvedMember, expectedType)
            }

            return buildStaticCallOrTypeReference(
                context,
                context.namespace.resolveSymbol(name) { "$position" },
                expectedType)
        }

        val resolvedReceiver = receiver.resolve(context, null)
        val receiverType = resolvedReceiver.getType().resolve(context.namespace) // TODO: resolve() should not be necessary here.
        return when (receiverType) {
            is MetaType -> {
                val resolvedMember = receiverType.type.resolveSymbol(name) { "$position" }
                buildStaticCallOrTypeReference(context, resolvedMember, expectedType)
            }
            is Classifier -> {
                val resolvedMember = receiverType.resolveSymbol(name) { "$position" }
                buildMethodCall(context, resolvedReceiver, resolvedMember, expectedType)
            }
            else -> throw IllegalStateException(
                "$position: Type '$receiverType' (${receiverType::class}) of resolved receiver '$resolvedReceiver' must be classifier for resolving '$name'")
        }
    }

    fun buildStaticCallOrTypeReference(context: ResolutionContext, resolvedMember: Classifier, expectedType: Type?): Expression =
        when(resolvedMember) {
            is TypedCallable -> {
                require(resolvedMember.static) {
                    "$position: Can't make static call to instance method '$resolvedMember'"
                }
                buildCallExpression(context, null, resolvedMember, expectedType)
            }
            is Type -> {
                require(children.isEmpty()) {
                    "$position: Types can't have function parameters; got $children"
                }
                require(!parens) {
                    "$position: Types shouldn't be followed by empty parens"
                }
                LiteralExpression(position, resolvedMember)
            }
            else ->
                throw IllegalStateException("Unrecognized resolved member: '$resolvedMember'")
        }

    fun buildCallExpression(context: ResolutionContext, resolvedReceiver: Expression?, resolvedMethod: TypedCallable, expectedType: Type?): CallExpression {
        val arrangedChildren = arrangeChildren(resolvedMethod)
        val resolvedChildren = mutableListOf<Expression?>()

        val genericTypeResolverState = GenericTypeResolverState {
            "$position: Resolving $this"
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