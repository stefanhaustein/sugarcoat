package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Definition
import org.kobjects.sugarcoat.fn.Callable
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.RootContext
import org.kobjects.sugarcoat.base.RuntimeContext
import org.kobjects.sugarcoat.base.Type

abstract class AbstractClassifierDefinition(
    override val parent: Definition?,
    override val name: String
): Definition, RuntimeContext, Type {
    val definitions = mutableMapOf<String, Definition>()
    val impls = mutableListOf<ImplDefinition>()

    override fun addDefinition(value: Definition) {
        if (value.name.isEmpty()) {
            impls.add(value as ImplDefinition)
        } else {
            require(!definitions.contains(value.name)) { "Symbol defined already: '${value.name}'" }
            definitions[value.name] = value
        }
    }

    override fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: RuntimeContext): RuntimeContext {
        val def = definitions[name]
        return when (def) {
            null -> ((parent ?: RootContext) as RuntimeContext).evalSymbol(name, children, parameterContext)
            is Callable -> def.call(null, children, parameterContext)
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