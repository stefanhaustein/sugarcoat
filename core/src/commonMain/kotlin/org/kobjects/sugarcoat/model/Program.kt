package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.parser.Position
import org.kobjects.sugarcoat.type.MetaType
import org.kobjects.sugarcoat.type.Type

class Program(
    val printFn: (String) -> Unit = ::print
) : Classifier(null, "", RootContext) {

    val impls = mutableMapOf<Pair<Type, TraitDefinition>, ImplDefinition>()



    fun findImpl(source: Type, target: Type): ImplDefinition {
        val resolvedSource = if (source is MetaType && source.type is ObjectDefinition) source.type else source
        return impls[resolvedSource to target] ?: throw IllegalStateException("No impl found that maps $source to $target; available: ${impls.keys}")
    }


    override fun toString() = "program $name"

    fun serialize(): String {
        val sb = CodeWriter()
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


    override fun serialize(writer: CodeWriter) {
        for (definition in definitions.values) {
            definition.serialize(writer)
            writer.newline()
        }
    }

}