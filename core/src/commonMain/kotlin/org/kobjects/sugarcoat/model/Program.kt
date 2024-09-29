package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.GlobalRuntimeContext
import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class Program(
    val printFn: (String) -> Unit = ::print
) : ResolvedType, AbstractClassifierDefinition(null, "") {

    val impls = mutableListOf<ImplDefinition>()

    override fun addDefinition(value: Namespace) {
        if (value.name.isEmpty()) {
            impls.add(value as ImplDefinition)
        } else {
            super.addDefinition(value)
        }
    }
/*
    override fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: RuntimeContext): Any {
        return
        else super.evalSymbol(name, children, parameterContext)
    }
*/
    override fun resolve(): ResolvedType = this


    fun run(vararg parameters: Any): Any {

        return (resolve("main") as FunctionDefinition).call(
            null,
            parameters.map { ParameterReference("", LiteralExpression(it)) },
            LocalRuntimeContext(
                GlobalRuntimeContext(printFn),
                this,
                null))

    }

}