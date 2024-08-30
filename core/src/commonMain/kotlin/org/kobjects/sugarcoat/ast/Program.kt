package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.ProgramContext

class Program : ResolvedType, Namespace {
    val functions = mutableMapOf<String, FunctionDefinition>()
    val types = mutableMapOf<String, Type>()

    override fun toString() =
        buildString {
            for ((name, fn) in functions) {
                append("fn $name")
                append(fn)
                append("\n")
            }
        }

    fun run(vararg parameters: Any, printFn: (String) -> Unit = { print(it) }): Any {
        val programContext = ProgramContext(this, printFn)

        return functions["main"]?.call(programContext, parameters.map { ParameterReference("", LiteralExpression(it)) }, programContext) ?: throw IllegalStateException("main function not found.")

    }

    fun addFunction(name: String, definition: FunctionDefinition) {
        functions[name] = definition
    }

    fun addType(name: String, type: Type) {
        types[name] = type
    }
}