package org.kobjects.sugarcoat.datatype

import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.base.Type

abstract class NativeType(
    name: String,

) : Classifier(null, name), ResolvedType {

    fun addNativeMethod(
        returnType: Type,
        name: String,
        vararg args: Pair<String, Type>,
        op: (NativeArgList) -> Any
    ) {
        addChild(NativeFunction(this, false, returnType, name, args, op))
    }

    fun addNativeFunction(
        returnType: Type,
        name: String,
        vararg args: Pair<String, Type>,
        op: (NativeArgList) -> Any
    ) {
        addChild(NativeFunction(this, true, returnType, name, args, op))
    }

    override fun toString(): String = "native $name"

    override fun serialize(sb: StringBuilder) {
        sb.append("native type $name\n")
        serializeBody(sb)
    }
}