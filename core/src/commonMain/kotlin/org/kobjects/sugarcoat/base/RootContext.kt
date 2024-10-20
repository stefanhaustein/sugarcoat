package org.kobjects.sugarcoat.base

import org.kobjects.sugarcoat.ast.LambdaExpression
import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.datatype.BoolType
import org.kobjects.sugarcoat.datatype.F64Type
import org.kobjects.sugarcoat.datatype.I64RangeType
import org.kobjects.sugarcoat.datatype.I64Type
import org.kobjects.sugarcoat.datatype.NativeFunction
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

    fun addControl(name: String, type: Type, vararg parameters: Pair<String, Type>, action: (List<ParameterReference>, LocalRuntimeContext) -> Any) {

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
                get() = FunctionType(type, parameters.map { it.second })

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

        addNativeFunction(F64Type,  "sqrt", ParameterDefinition("value", F64Type)) { sqrt(it.f64(0)) }

        addControl("for", VoidType, "iterable" to FunctionType(VoidType)) { params, context ->
            val range = params[0].value.eval(context) as LongRange
            for (value in range) {
                (params[1].value as LambdaExpression).lambda.call(null, listOf(
                    ParameterReference("", LiteralExpression(value))
                ), context)
            }
        }

    }
}