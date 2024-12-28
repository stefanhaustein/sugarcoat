package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.CodeWriter
import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.ImplInstance
import org.kobjects.sugarcoat.model.TraitDefinition

class DelegateToImpl(
    override val parent: TraitDefinition,
    fallback: Classifier?,
    name: String,
    override var type: FunctionType,
) : Classifier(parent, name, emptyList(), fallback), Callable {
    override fun serialize(writer: CodeWriter) {
        writer.append(name)
        writer.append("(<TBD>)")
    }

    override val static: Boolean
        get() = false

    override fun call(
        receiver: Any?,
        children: List<Expression?>,
        parameterScope: LocalRuntimeContext
    ): Any =
        ((receiver as ImplInstance).implDefinition.definitions[name] as Callable).call(receiver.wrapped, children, parameterScope)



    override fun resolveSignatures() {
        type = type.resolveType(parent)
    }

    override fun toString() = "fn $name(<tbd>)"
}