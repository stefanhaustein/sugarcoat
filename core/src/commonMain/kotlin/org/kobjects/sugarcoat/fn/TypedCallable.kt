package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.type.Typed

interface TypedCallable : Callable, Typed {
    override val type: FunctionType
}