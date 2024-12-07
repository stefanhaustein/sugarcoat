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
    var parameters: List<ParameterDefinition>,
    var returnType: Type,
) : Classifier(parent, name, fallback), Callable {
    override fun serialize(writer: CodeWriter) {
        writer.append(name)
        writer.append("(<TBD>)")
    }

    override val type: FunctionType
        get() = FunctionType(returnType, parameters)

    override val static: Boolean
        get() = false

    override fun call(
        receiver: Any?,
        children: List<Expression?>,
        parameterScope: LocalRuntimeContext
    ): Any =
        ((receiver as ImplInstance).implDefinition.definitions[name] as Callable).call(receiver, children, parameterScope)

    override fun toString() = "fn $name(<tbd>)"
}