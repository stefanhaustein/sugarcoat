package org.kobjects.sugarcoat

interface Evaluable {
    fun eval(context: RuntimeContext): RuntimeContext

    fun evalDouble(context: RuntimeContext) = (eval(context) as DoubleContext).value
    fun evalBoolean(context: RuntimeContext) = (eval(context) as BooleanContext).value
    fun evalLong(context: RuntimeContext) = (eval(context) as LongContext).value

    fun stringify(stringBuilder: StringBuilder, parentPrecedence: Int) {
        stringBuilder.append(this)
    }
}