package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.ProgramContext

class Program(
    val functions: Map<String, FunctionDefinition>
)  {

    override fun toString() =
        buildString {
            for ((name, fn) in functions) {
                append("fn $name")
                append(fn)
                append("\n")
            }
        }

    fun run(vararg parameters: Any, printFn: (String) -> Unit = { print(it) }): Any {

        return functions["main"]?.call(parameters.map { ParameterReference("", LiteralExpression(it)) }, ProgramContext(this, printFn)) ?: throw IllegalStateException("main function not found.")

    }
}