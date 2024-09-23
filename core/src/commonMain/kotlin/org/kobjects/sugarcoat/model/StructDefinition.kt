package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.base.Namespace
import org.kobjects.sugarcoat.base.ResolvedType
import org.kobjects.sugarcoat.fn.ParameterConsumer
import org.kobjects.sugarcoat.ast.ParameterReference

class StructDefinition(
    parent: Namespace,
    name: String,
    val constructorName: String = "create"
): ResolvedType, AbstractClassifierDefinition(parent, name) {



}