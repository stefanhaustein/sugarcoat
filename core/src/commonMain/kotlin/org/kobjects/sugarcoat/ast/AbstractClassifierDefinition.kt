package org.kobjects.sugarcoat.ast

import org.kobjects.sugarcoat.runtime.RootContext
import org.kobjects.sugarcoat.runtime.RuntimeContext

abstract class AbstractClassifierDefinition(
    override val parent: Definition?
): Definition, RuntimeContext {
    val definitions = mutableMapOf<String, Definition>()

    override fun addDefinition(name: String, value: Definition) {
        require(!definitions.contains(name)) { "Symbol defined already: $name" }
        definitions[name] = value
    }

    override fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext {
        val def = definitions[name]
        return when (def) {
            null -> ((parent ?: RootContext) as RuntimeContext).evalSymbol(name, children, parameterContext)
            is Callable -> def.call(this, children, parameterContext)
            is RuntimeContext -> def
            else -> throw IllegalStateException("$name: $def is not evaluable.")
        }
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

}