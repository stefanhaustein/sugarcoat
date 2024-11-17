package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.Type

class Program(
    val printFn: (String) -> Unit = ::print
) : Classifier(null, "", RootContext) {

    val impls = mutableMapOf<Pair<Type, TraitDefinition>, ImplDefinition>()



    fun findImpl(source: Type, target: Type): ImplDefinition {
        return impls[source to target] ?: throw IllegalStateException("No impl found that maps $source to $target; available: ${impls.keys}")
    }


    override fun toString() = "program $name"

    fun serialize(): String {
        val sb = StringBuilder()
        serialize(sb)
        return sb.toString()
    }

    fun run(vararg parameters: Any): Any {
        val globalRuntimeContext = GlobalRuntimeContext(this, printFn)
        program.initialize(globalRuntimeContext)
        return (resolveSymbol("main") as FunctionDefinition).call(
            null,
            parameters.map { LiteralExpression(Position("main argument"), it) },
            LocalRuntimeContext(globalRuntimeContext, null))
    }

    fun resolveAll() {
        for (pass in ResolutionPass.entries) {
            resolutionPass(this, pass)
        }
    }


    override fun serialize(sb: StringBuilder) {
        for (definition in definitions.values) {
            definition.serialize(sb)
            sb.append("\n")
        }
    }

}