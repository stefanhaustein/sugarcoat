package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.type.MetaType
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.Namespace
import org.kobjects.sugarcoat.model.TraitDefinition
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.GenericTypeResolver

var indent = ""

class UnresolvedSymbolExpression(
    position: Position,
    val receiver: Expression?,
    val name: String,
    val parens: Boolean,
    val children: List<ParameterReference>,
) : Expression(position) {

    constructor(position: Position, receiver: Expression, name: String, vararg children: Expression) : this(
        position,
        receiver,
        name,
        children.size > 0,
        children.map { ParameterReference("", it) })

    constructor(position: Position, name: String, vararg children: Expression) : this(
        position,
        null,
        name,
        children.size > 0,
        children.map { ParameterReference("", it) })

    override fun eval(context: LocalRuntimeContext) = throw UnsupportedOperationException()

    override fun serialize(writer: CodeWriter) {
        writer.writeInvocation(
            receiver,
            name,
            children.map { it.name to it.value } )
    }

    override fun getType() = throw UnsupportedOperationException()

    fun arrangeChildren(callable: Callable): List<Expression?> {
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
        resolvedMember: Namespace,
        expectedType: Type?
    ): Expression {
        require(resolvedMember is Callable) {
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
        if (receiver == null) {
            val localVariable = context.resolveLocalVariableOrNull(position, name)
            if (localVariable != null) {
                return buildCallExpression(context, null, localVariable, expectedType)
            }

            val self = context.resolveLocalVariableOrNull(position, "self")
            
            val resolvedMember = if (self == null) null else
                (self.type.returnType as Namespace).resolveSymbolOrNull(name)

            if (resolvedMember is Callable && !resolvedMember.static) {
                val selfExpression = CallExpression(position, null, self as Callable, emptyList())
                return buildMethodCall(context,  selfExpression, resolvedMember, expectedType)
            }

            return buildStaticCallOrTypeReference(
                context,
                context.namespace.resolveSymbol(name) { "$position" },
                expectedType)
        }

        val resolvedReceiver = receiver.resolve(context, null)
        val receiverType = resolvedReceiver.getType().resolveType(context.namespace) // TODO: resolve() should not be necessary here.
        return when (receiverType) {
            is MetaType -> {
                if (name == "[]") {
                    buildTypeReferenceWithResolveGenerics(context, resolvedReceiver, expectedType)
                } else if (name == "()") {
                    buildStaticCallOrTypeReference(context, receiverType.type, expectedType)
                } else {
                    val resolvedMember = receiverType.type.resolveSymbol(name) { "$position" }
                    buildStaticCallOrTypeReference(context, resolvedMember, expectedType)
                }
            }
            is Namespace -> {
                val resolvedMember = receiverType.resolveSymbol(name) { "$position" }
                buildMethodCall(context, resolvedReceiver, resolvedMember, expectedType)
            }
            else -> throw IllegalStateException(
                "$position: Type '$receiverType' (${receiverType::class}) of resolved receiver '$resolvedReceiver' must be classifier for resolving '$name'.")
        }
    }

    fun buildTypeReferenceWithResolveGenerics(
        context: ResolutionContext,
        resolvedReceiver: Expression,
        expectedType: Type?
    ): Expression {
        require(resolvedReceiver is LiteralExpression) {
            "$position: Literal expression expected for generic type specification"
        }
        // Migrate to something like "ResolveAsType"
        val classifier = resolvedReceiver.value as Classifier
        val resolvedTypes = mutableListOf<Type>()
        for (child in children.map { it.value }) {
            require(child is UnresolvedSymbolExpression && !child.parens && child.children.isEmpty())
            val resolved = context.namespace.resolveSymbol(child.name)
            require(resolved is Type)
            resolvedTypes.add(resolved)
        }
        val resolvedGenericType = classifier.typed(*resolvedTypes.toTypedArray())
        return LiteralExpression(position, resolvedGenericType)
    }

    fun buildStaticCallOrTypeReference(
        context: ResolutionContext,
        resolvedMember: Namespace,
        expectedType: Type?
    ): Expression =
        when(resolvedMember) {
            is Callable -> {
                require(resolvedMember.static) {
                    "$position: Can't make static call to instance method '$resolvedMember'"
                }
                buildCallExpression(context,null, resolvedMember, expectedType)
            }
            is Classifier -> {
                if (parens) {
                    when (resolvedMember) {
                        is TraitDefinition -> {
                            require(children.size == 1) {
                                "$position: Exactly one parameter expected."
                            }
                            val parameter = children.first().value.resolve(context, null)
                            val impl = context.namespace.program.findImpl(parameter.getType(), resolvedMember)
                            AsExpression(position, parameter, impl)
                        }
                        else -> {
                            require(resolvedMember.constructorName.isNotEmpty()) {
                                throw IllegalArgumentException("$position: Constructor-like calls not supported on $resolvedMember.")
                            }
                            val ctor = resolvedMember.resolveSymbol(resolvedMember.constructorName) { "$position" }
                            require (ctor is Callable) {
                                "$position: Constructor $ctor for $resolvedMember is not callable."
                            }
                            buildCallExpression(context, null, ctor, expectedType)
                        }
                    }

                } else {
                    val result = LiteralExpression(position, resolvedMember)
                    if (expectedType is TraitDefinition && result.getType() != expectedType) {
                        val impl = context.namespace.program.findImpl(result.getType(), expectedType)
                        AsExpression(position, result, impl)
                    } else {
                        result
                    }
                }
            }
            else ->
                throw IllegalStateException("Unrecognized resolved member: '$resolvedMember'")
        }


    fun buildCallExpression(
        context: ResolutionContext,
        resolvedReceiver: Expression?,
        resolvedMethod: Callable,
        expectedType: Type?
    ): Expression {
        return withImpliedTransformations(context, expectedType, resolvedMethod.type.returnType) { resolvedExpectedType ->
            buildCallExpressionImpl(context, resolvedReceiver, resolvedMethod, resolvedExpectedType)
        }
    }

    fun buildCallExpressionImpl(
        context: ResolutionContext,
        resolvedReceiver: Expression?,
        resolvedMethod: Callable,
        expectedType: Type?
    ): CallExpression {
        val arrangedChildren = arrangeChildren(resolvedMethod)
        val resolvedChildren = mutableListOf<Expression?>()

        val genericTypes = resolvedMethod.type.getGenericTypes()

        if (name == "pair") {
            println("bp")
        }

        val genericTypeResolver = GenericTypeResolver {
           "$position: Resolving '$this'"
         }
        resolvedMethod.type.returnType.match(expectedType, genericTypeResolver) {
            "$position: Return type '${resolvedMethod.type.returnType}' for $this does not match expected type $expectedType"
        }
        //
        //resolvedMethod.type.returnType.resolveGenerics(genericTypeResolver)

        for ((i, parameter) in resolvedMethod.type.parameterTypes.withIndex()) {

            val parameterRestType = parameter.restType()
            val expectedParameterType = parameterRestType.resolveGenerics(genericTypeResolver)

            if (parameterRestType.getGenericTypes().isNotEmpty()) {
                println("${indent}buildCallExpression for parameter '$parameter' of '$name'")
                println("$indent > expectedParameterType: $parameterRestType")
                println("$indent > expectedParameterType with resolved generics: ${expectedParameterType}")
                println("$indent > unresolved child: ${arrangedChildren[i]}")
                println("$indent > generic type map: $genericTypeResolver")

                indent += "  "
            }

            val resolvedChild = arrangedChildren[i]?.resolve(context, expectedParameterType)
            if (resolvedChild != null) {
                resolvedChild.getType().match(expectedParameterType, genericTypeResolver) {
                    "$position: Type '${resolvedChild.getType()}' of expression $resolvedChild  does not match expected type '$expectedParameterType' for parameter '$parameter' of method '$this'; generic type map: $genericTypeResolver"
                }
                // parameter.restType().resolveGenerics(genericTypeResolver)
            }

            if (genericTypes.isNotEmpty()) {
                indent = indent.dropLast(2)
                println("$indent < resolvedChild: $resolvedChild")
                println("$indent < resolvedChild type: ${resolvedChild?.getType()}")
                println("$indent < updated generic type map: $genericTypeResolver")
            }
            resolvedChildren.add(resolvedChild)
        }

        resolvedMethod.type.returnType.match(expectedType, genericTypeResolver) {
            "$position: Return type '${resolvedMethod.type.returnType}' for $this does not match expected type $expectedType"
        }
        //val resolvedReturnType = resolvedMethod.type.returnType.resolveGenerics(genericTypeResolver, expectedType)
        return CallExpression(position, resolvedReceiver, resolvedMethod, resolvedChildren)
    }

}