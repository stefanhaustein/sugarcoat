package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.RuntimeContext

class Program(
    val printFn: (String) -> Unit = ::print
) : ResolvedType, AbstractClassifierDefinition(null, "") {



    override fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext {
        return if (name == "print") {
                printFn(children.joinToString { it.value.eval(parameterContext).toString() })
                VoidType.Instance
        }
        else super.evalSymbol(name, children, parameterContext)
    }

    override fun resolve(): ResolvedType = this


    fun run(vararg parameters: Any): Any {

        return (definitions["main"] as FunctionDefinition).call(null, parameters.map { ParameterReference("", LiteralExpression(it)) }, this) ?: throw IllegalStateException("main function not found.")

    }

}