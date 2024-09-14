package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.ast.AbstractClassifierDefinition
import org.kobjects.sugarcoat.ast.Callable
import org.kobjects.sugarcoat.ast.Definition
import org.kobjects.sugarcoat.ast.ParameterConsumer
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.ast.ResolvedType
import org.kobjects.sugarcoat.ast.Type
import org.kobjects.sugarcoat.runtime.RuntimeContext

abstract class NativeType(
    name: String,

) : AbstractClassifierDefinition(null, name), ResolvedType {


    fun addNativeMethod(
        returnType: Type,
        name: String,
        vararg args: Pair<String, Type>,
        op: (List<RuntimeContext>) -> RuntimeContext
        ) {
        addDefinition(name, NativeMethod(this, returnType, name, args, op))
    }


}