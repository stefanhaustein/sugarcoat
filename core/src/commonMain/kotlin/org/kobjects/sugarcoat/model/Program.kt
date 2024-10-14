package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.LiteralExpression
import org.kobjects.sugarcoat.fn.FunctionDefinition
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.GlobalRuntimeContext
import org.kobjects.sugarcoat.base.Element
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext

class Program(
    val printFn: (String) -> Unit = ::print
) : ResolvedType, Classifier(null, "") {

    val impls = mutableListOf<ImplDefinition>()

    override fun addChild(value: Element) {
        if (value.name.isEmpty()) {
            impls.add(value as ImplDefinition)
        } else {
            super.addChild(value)
        }
    }
/*
    override fun evalSymbol(name: String, children: List<ParameterReference>, parameterContext: RuntimeContext): Any {
        return
        else super.evalSymbol(name, children, parameterContext)
    }
*/
    override fun resolve(): ResolvedType = this
    override fun toString() = "program $name"

    fun serialize(): String {
        val sb = StringBuilder()
        serialize(sb)
        return sb.toString()
    }

    fun run(vararg parameters: Any): Any {

        return (resolve("main") as FunctionDefinition).call(
            null,
            parameters.map { ParameterReference("", LiteralExpression(it)) },
            LocalRuntimeContext(
                GlobalRuntimeContext(printFn),
                this,
                null))

    }

    override fun findImpl(source: ResolvedType, target: ResolvedType): ImplDefinition {
        for (impl in impls) {
            if (impl.struct.resolve() == source && impl.trait.resolve() == target) {
                return impl
            }
        }
        throw IllegalStateException("Unable to find map from '$source' to '$target'; available: $impls")
    }

    override fun serialize(sb: StringBuilder) {
        for (definition in definitions.values) {
            definition.serialize(sb)
            sb.append("\n")
        }
    }

}