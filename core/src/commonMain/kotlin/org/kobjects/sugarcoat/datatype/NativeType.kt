package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.model.AbstractClassifierDefinition
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.base.RuntimeContext

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