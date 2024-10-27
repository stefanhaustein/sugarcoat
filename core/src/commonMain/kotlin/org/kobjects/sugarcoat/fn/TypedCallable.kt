package org.kobjects.sugarcoat.fn

import org.kobjects.sugarcoat.base.Typed

interface TypedCallable : Callable, Typed {
    override val type: FunctionType
}