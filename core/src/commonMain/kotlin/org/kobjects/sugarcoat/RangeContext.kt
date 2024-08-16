package org.kobjects.sugarcoat

class RangeContext(
    val value: LongRange
) : RuntimeContext {
    override fun evalSymbol(
        name: String,
        children: List<Parameter>,
        parameterContext: RuntimeContext
    ): RuntimeContext {
        TODO("Not yet implemented")
    }
}