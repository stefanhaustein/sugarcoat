package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.fn.ParameterReference
import org.kobjects.sugarcoat.base.RuntimeContext

class Program(
    val printFn: (String) -> Unit = ::print
) : AbstractClassifierDefinition(null, "") {



    override fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext {
        return if (name == "print") {
                printFn(children.joinToString { it.value.eval(parameterContext).toString() })
                VoidType.Instance
        }
        else super.evalSymbol(name, children, parameterContext)
    }


    fun run(vararg parameters: Any): Any {

        return (definitions["main"] as FunctionDefinition).call(this, parameters.map { ParameterReference("", LiteralExpression(it)) }, this) ?: throw IllegalStateException("main function not found.")

    }

}