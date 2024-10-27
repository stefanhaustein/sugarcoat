package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.ImplInstance
import org.kobjects.sugarcoat.model.TraitDefinition

class DelegateToImpl(
    override val parent: TraitDefinition,
    fallback: Classifier?,
    name: String,
    var parameters: List<ParameterDefinition>,
    var returnType: Type,
) : Classifier(parent, name, fallback), TypedCallable {
    override fun serialize(sb: StringBuilder) {
        sb.append(name)
        sb.append("(<TBD>)")
    }

    override val type: FunctionType
        get() = FunctionType(returnType, parameters.map{it.type})

    override val static: Boolean
        get() = false

    override fun call(
        receiver: Any?,
        children: List<ParameterReference>,
        parameterScope: LocalRuntimeContext
    ): Any =
        ((receiver as ImplInstance).implDefinition.definitions[name] as Callable).call(receiver, children, parameterScope)

    override fun toString() = "fn $name(<tbd>)"
}