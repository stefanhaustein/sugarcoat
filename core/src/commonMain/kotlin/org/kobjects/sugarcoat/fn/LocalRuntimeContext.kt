package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.GlobalRuntimeContext
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.base.Typed
import org.kobjects.sugarcoat.model.Classifier
import org.kobjects.sugarcoat.model.Instance

class LocalRuntimeContext(
    val globalRuntimeContext: GlobalRuntimeContext,
    val instance: Any?
) {
    val symbols = mutableMapOf<String, Any>()

}