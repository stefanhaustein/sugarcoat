package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.Scope

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

    override fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: Scope): Scope {
        return if (name == "print") {
                printFn(children.joinToString { it.value.eval(parameterContext).toString() })
                VoidType.VoidInstance
        }
        else super.evalSymbol(name, children, parameterContext)
    }

    override fun resolve(): ResolvedType = this


    fun run(vararg parameters: Any): Any {

        return (definitions["main"] as FunctionDefinition).call(null, parameters.map { ParameterReference("", LiteralExpression(it)) }, this) ?: throw IllegalStateException("main function not found.")

    }

}