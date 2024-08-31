package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.ProgramContext

class Program : ResolvedType, Definition {
    val definitions = mutableMapOf<String, Definition>()

    override fun addDefinition(name: String, value: Definition) {
        definitions[name] = value
    }

    override fun toString() =
        buildString {
            for ((name, definition) in definitions) {
                when (definition) {
                    is FunctionDefinition -> {
                        append("fn $name$definition\n")
                    }
                }
            }
        }

    fun run(vararg parameters: Any, printFn: (String) -> Unit = { print(it) }): Any {
        val programContext = ProgramContext(this, printFn)

        return (definitions["main"] as FunctionDefinition).call(programContext, parameters.map { ParameterReference("", LiteralExpression(it)) }, programContext) ?: throw IllegalStateException("main function not found.")

    }

}