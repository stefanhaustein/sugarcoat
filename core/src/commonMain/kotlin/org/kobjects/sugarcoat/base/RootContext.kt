package org.kobjects.sugarcoat.base

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.LambdaExpression
import org.kobjects.sugarcoat.ast.ListExpression
import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.UnresolvedSymbolExpression
import org.kobjects.sugarcoat.datatype.AnyType
import org.kobjects.sugarcoat.datatype.BoolType
import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64RangeType
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.datatype.PairType
import org.kobjects.sugarcoat.datatype.StringType
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.model.Classifier
import kotlin.math.sqrt

object RootContext : Classifier(null, "") {
    override fun serialize(sb: StringBuilder) {
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

        addControl(
            "=",
            VoidType,
            ParameterDefinition("target", AnyType),
            ParameterDefinition("source", AnyType),
        ) { children, parameterContext ->
            require(children.size == 2) { "Two parameters expected for assignment" }
            val target = (children.first() as LiteralExpression).value as String
            (parameterContext as LocalRuntimeContext).symbols[target] =
                children.last()!!.eval(parameterContext)
            Unit
        }

        addControl(
            "for",
            VoidType,
            ParameterDefinition("iterable", ListType),
            ParameterDefinition("body", FunctionType(VoidType, ParameterDefinition("iteratpr", I64Type))),
        ) { params, context ->
            evalFor(params, context)
        }

        addControl(
            "if",
            VoidType,
            ParameterDefinition("condition", BoolType),
            ParameterDefinition("then", FunctionType(VoidType)),
            ParameterDefinition("elif", VoidType, repeated = true),
            ParameterDefinition("else", FunctionType(VoidType), false, LiteralExpression(Unit)),
        ) { params, context ->
            evalIf(params, context)
        }

        addControl(
            "print",
            VoidType,
            ParameterDefinition("value", StringType, repeated = true)
        ) { children, context ->
            context.globalRuntimeContext.printFn((children[0]!!.eval(context) as List<Any>).joinToString())
        }

        addControl(
            "range",
            I64RangeType,
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
            "seq",
            ListType,
            ParameterDefinition("value", VoidType, true),
        ) { children, parameterContext ->
            children.fold<Expression?, Any>(Unit) { _, current ->
                current!!.eval(parameterContext)
            }
        }

        addControl(
            "pair",
            PairType(AnyType, AnyType),
            ParameterDefinition("first", AnyType),
            ParameterDefinition("second", AnyType),
        ) { children, parameterContext ->
            require(children.size == 2) { "Two parameters expected for 'pair'." }
            Pair(children[0]!! /*!!.eval(parameterContext) */, children[1]!! /*.eval(parameterContext) */)
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
        val range = children[0]!!.eval(parameterContext) as LongRange
        for (value in range) {
            (children[1] as LambdaExpression).lambda.call(null, listOf(
                LiteralExpression(value)), parameterContext)
        }
        return Unit
    }

    fun evalIf(children: List<Expression?>, parameterContext: LocalRuntimeContext): Any {
        if (children[0]!!.evalBoolean(parameterContext)) {
            return children[1]!!.eval(parameterContext)
        }
        if (children.size > 2 && children[2] != null) {
            require(children[2] is ListExpression) {
                "List expression expected for elseif but got ${children[1]}; all children: $children"
            }
            val elif = children[2] as ListExpression
            for (pairFn in elif.elements) {
                val pair = pairFn.eval(parameterContext) as Pair<Expression, Expression>
                if (pair.first.evalBoolean(parameterContext)) {
                    return pair.second.eval(parameterContext)
                }

            }
        }
        if (children.size == 4 && children[3] != null) {
            return children[3]!!.eval(parameterContext)
        }
        return Unit
    }

}