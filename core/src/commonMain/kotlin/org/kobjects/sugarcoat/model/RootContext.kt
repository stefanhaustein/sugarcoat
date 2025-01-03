package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ListExpression
import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.datatype.AnyType
import org.kobjects.sugarcoat.datatype.BoolType
import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64RangeType
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.datatype.IterableTrait
import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.datatype.MutableListType
import org.kobjects.sugarcoat.datatype.PairType
import org.kobjects.sugarcoat.datatype.StringType
import org.kobjects.sugarcoat.datatype.ToStringTrait
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.GenericType
import kotlin.math.sqrt

object RootContext : Namespace(null, "") {
    override fun serialize(writer: CodeWriter) {
        throw UnsupportedOperationException()
    }

    override fun toString(): String = "Root Context"

    init {
        addChild(BoolType)
        addChild(F64Type)
        addChild(StringType)
        addChild(I64Type)
        addChild(I64RangeType)
        addChild(VoidType)
        addChild(ListType(GenericType("E")))
        addChild(MutableListType(GenericType("E")))
        addChild(PairType(GenericType("F"), GenericType("S")))

        addChild(ToStringTrait)

        addControl(
            "=",
            VoidType,
            ParameterDefinition("target", AnyType),
            ParameterDefinition("source", AnyType),
        ) { children, parameterContext ->
            require(children.size == 2) { "Two parameters expected for assignment" }
            val target = (children.first() as LiteralExpression).value as String
            parameterContext.set(target, children.last()!!.eval(parameterContext))
            Unit
        }

        val forGenericType = GenericType("I")
        addControl(
            "for",
            VoidType,
            ParameterDefinition("iterable", IterableTrait(forGenericType)),
            ParameterDefinition("body", FunctionType(VoidType, ParameterDefinition("iterator", forGenericType))),
        ) { params, context ->
            evalFor(params, context)
        }

        val ifGenericType = GenericType("R")
        addControl(
            "if",
            ifGenericType,
            ParameterDefinition("condition", BoolType),
            ParameterDefinition("then", FunctionType(ifGenericType)),
            ParameterDefinition("elif", PairType(FunctionType(BoolType), FunctionType(ifGenericType)), repeated = true),
            ParameterDefinition("else", FunctionType(ifGenericType), false, LiteralExpression(
                Position("Implied void else branch"), Unit)),
        ) { params, context ->
            evalIf(params, context)
        }

        addControl(
            "print",
            VoidType,
            ParameterDefinition("value", ToStringTrait, repeated = true)
        ) { children, context ->
            context.globalRuntimeContext.printFn((children[0]!!.eval(context) as List<ImplInstance>).joinToString { (it.implDefinition.definitions["toString"] as Callable).call(it.wrapped, emptyList(), context).toString()  } )
        }

        addControl(
            "range",
            ListType(I64Type),
            ParameterDefinition("a", I64Type),
            ParameterDefinition("b", I64Type),
        ) { children, parameterContext ->
            when (children.size) {
                1 ->
                    LongRange(
                        0,
                        children[0]!!.evalLong(parameterContext) - 1

                    )

                2 ->
                    LongRange(
                        children.first()!!.evalLong(parameterContext),
                        children.last()!!.evalLong(parameterContext) - 1

                    )

                else -> throw IllegalArgumentException("2 or 3 parameter expected for range, but got ${children.size}")
            }
        }

        addControl(
            "while",
            VoidType,
            ParameterDefinition("condition", FunctionType(BoolType)),
            ParameterDefinition("body", FunctionType(VoidType))
        ) { children, parameterContext ->
            require(children.size == 2) { "Two parameters expected for 'while'." }
            while (children[0]!!.evalBoolean(parameterContext)) children[1]!!.eval(
                parameterContext
            )
        }

        addNativeFunction(F64Type,  "sqrt", ParameterDefinition("value", F64Type)) { sqrt(it.f64(0)) }
    }


    fun evalFor(children: List<Expression?>, parameterContext: LocalRuntimeContext): Any {
        val iterableImpl = children[0]!!.eval(parameterContext) as ImplInstance
        val iterable = iterableImpl.wrapped as Iterable<Any>
        for (value in iterable) {
            (children[1]!!.eval(parameterContext) as Callable).call(parameterContext.instance, listOf(
                LiteralExpression(children[1]!!.position, value!!)), parameterContext)
        }
        return Unit
    }

    fun evalIf(children: List<Expression?>, parameterContext: LocalRuntimeContext): Any {
        if (children[0]!!.evalBoolean(parameterContext)) {
            val thenLambda = children[1]!!.eval(parameterContext)
            return (thenLambda as Callable).call(parameterContext.instance, emptyList(), parameterContext)
        }
        if (children.size > 2 && children[2] != null) {
            require(children[2] is ListExpression) {
                "List expression expected for elseif but got ${children[1]}; all children: $children"
            }
            val elif = children[2] as ListExpression
            for (pairFn in elif.elements) {
                println("Current elif: $pairFn")
                val pair = pairFn.eval(parameterContext) as Pair<*, *>
                println("Resulting pair: $pair; ${pair.first!!::class} to ${pair.second!!::class} $")
                val conditionCallable = pair.first as Callable
                if (conditionCallable.call(parameterContext.instance, emptyList(), parameterContext) as Boolean) {
                    val thenCallable = pair.second as Callable
                    return thenCallable.call(parameterContext.instance, emptyList(), parameterContext)
                }
            }
        }
        if (children.size == 4 && children[3] != null) {
            val elseLambda = children[3]!!.eval(parameterContext)
            return (elseLambda as Callable).call(parameterContext.instance, emptyList(), parameterContext)
        }
        return Unit
    }

}