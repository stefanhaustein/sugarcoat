package org.kobjects.sugarcoat.base

import org.kobjects.sugarcoat.ast.LambdaExpression
import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.SymbolExpression
import org.kobjects.sugarcoat.datatype.AnyType
import org.kobjects.sugarcoat.datatype.BoolType
import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64RangeType
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.datatype.ListType
import org.kobjects.sugarcoat.datatype.StringType
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.Callable
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

    fun addControl(name: String, type: Type, vararg parameters: ParameterDefinition, action: (List<ParameterReference>, LocalRuntimeContext) -> Any) {

        addChild(object : Callable, Classifier(this, name, null), Typed {
            override val static: Boolean
                get() = true

            override fun call(
                receiver: Any?,
                children: List<ParameterReference>,
                parameterScope: LocalRuntimeContext
            ): Any {
                return action(children, parameterScope)
            }

            override fun serialize(sb: StringBuilder) {
                throw UnsupportedOperationException()
            }

            override val type: Type
                get() = FunctionType(type, parameters.map { it.type })

            override fun toString() = "control instruction '$name'"

        })
    }

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
                children.last().value.eval(parameterContext)
            Unit
        }

        addControl(
            "for",
            VoidType,
            ParameterDefinition("iterable", ListType),
            ParameterDefinition("body", FunctionType(VoidType)),
        ) { params, context ->
            evalFor(params, context)
        }

        addControl(
            "if",
            VoidType,
            ParameterDefinition("condition", BoolType),
            ParameterDefinition("then", FunctionType(VoidType)),
            ParameterDefinition("elif", VoidType, repeated = true),
            ParameterDefinition("else", FunctionType(VoidType)),
        ) { params, context ->
            evalIf(params, context)
        }

        addControl(
            "print",
            VoidType,
            ParameterDefinition("value", StringType, repeated = true)
        ) { children, context ->
            context.globalRuntimeContext.printFn(children.joinToString { it.value.eval(context).toString() } )
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
                        children[0].value.evalLong(parameterContext) - 1

                    )

                2 ->
                    LongRange(
                        children.first().value.evalLong(parameterContext),
                        children.last().value.evalLong(parameterContext) - 1

                    )

                else -> throw IllegalArgumentException("2 or 3 parameter expected for range, but got ${children.size}")
            }
        }

        addControl(
            "seq",
            ListType,
            ParameterDefinition("value", VoidType),
        ) { children, parameterContext ->
            children.fold<ParameterReference, Any>(Unit) { _, current ->
                current.value.eval(parameterContext)
            }
        }

        addControl(
            "while",
            VoidType,
            ParameterDefinition("condition", FunctionType(BoolType)),
            ParameterDefinition("body", FunctionType(VoidType))
        ) { children, parameterContext ->
            require(children.size == 2) { "Two parameters expected for 'while'." }
            while (children[0].value.evalBoolean(parameterContext)) children[1].value.eval(
                parameterContext
            )
        }

        addNativeFunction(F64Type,  "sqrt", ParameterDefinition("value", F64Type)) { sqrt(it.f64(0)) }
    }


    fun evalFor(children: List<ParameterReference>, parameterContext: LocalRuntimeContext): Any {
        val range = children[0].value.eval(parameterContext) as LongRange
        for (value in range) {
            (children[1].value as LambdaExpression).lambda.call(null, listOf(
                ParameterReference("", LiteralExpression(value))
            ), parameterContext)
        }
        return Unit
    }

    fun evalIf(children: List<ParameterReference>, parameterContext: LocalRuntimeContext): Any {
        if (children[0].value.evalBoolean(parameterContext)) {
            return children[1].value.eval(parameterContext)
        }
        for (i in 2 until children.size) {
            val child = children[i]
            val value = child.value
            when (child.name) {
                "elif" -> {
                    require (value is SymbolExpression && value.name == "pair")
                    if (value.children[0].value.evalBoolean(parameterContext)) {
                        return value.children[1].value.eval(parameterContext)
                    }
                }
                "else" -> {
                    return value.eval(parameterContext)
                }
                else -> throw IllegalStateException("else or elif expected; got: '${child.name}'")
            }
        }
        return Unit
    }

}