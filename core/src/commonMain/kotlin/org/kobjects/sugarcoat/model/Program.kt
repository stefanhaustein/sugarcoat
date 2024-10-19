package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.GlobalRuntimeContext
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class Program(
    val printFn: (String) -> Unit = ::print
) : ResolvedType, Classifier(null, "") {

    override fun resolve(): ResolvedType = this
    override fun toString() = "program $name"

    fun serialize(): String {
        val sb = StringBuilder()
        serialize(sb)
        return sb.toString()
    }

    fun run(vararg parameters: Any): Any {

        return (resolve("main") as FunctionDefinition).call(
            null,
            parameters.map { ParameterReference("", LiteralExpression(it)) },
            LocalRuntimeContext(
                GlobalRuntimeContext(this, printFn),
                this,
                null))

    }


    override fun serialize(sb: StringBuilder) {
        for (definition in definitions.values) {
            definition.serialize(sb)
            sb.append("\n")
        }
    }

}