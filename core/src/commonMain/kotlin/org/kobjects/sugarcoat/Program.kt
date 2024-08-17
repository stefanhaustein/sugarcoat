package org.kobjects.sugarcoat

import org.kobjects.sugarcoat.function.LambdaDeclaration

class Program(
    val functions: Map<String, LambdaDeclaration>
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

        return functions["main"]?.eval(parameters.map { ParameterReference("", Literal(it)) }, ProgramContext(this, printFn)) ?: throw IllegalStateException("main function not found.")

    }
}