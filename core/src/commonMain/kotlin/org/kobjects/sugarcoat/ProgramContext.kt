package org.kobjects.sugarcoat

class ProgramContext(
    val program: Program,
    val printFn: (String) -> Unit = ::print
) : RuntimeContext {

    override fun evalSymbol(name: String, children: List<Parameter>, parameterContext: RuntimeContext): Any =
        program.functions[name]?.eval(children, parameterContext) ?: when (name) {
            "for" -> evalFor(children, parameterContext)
            "if" -> evalIf(children, parameterContext)
            "print" -> printFn(children.joinToString { it.value.eval(parameterContext).toString() })
            "range" -> when (children.size) {
                1 -> LongRange(0, children[0].value.evalLong(parameterContext) - 1)
                2 -> LongRange(
                    children.first().value.evalLong(parameterContext),
                    children.last().value.evalLong(parameterContext) - 1
                )
                else -> throw IllegalArgumentException("2 or 3 parameter expected for range, but got ${children.size}")
            }.map { it.toDouble() }
            "seq" -> children.fold(Unit) { _, current -> current.value.eval(parameterContext) }
            "=" -> {
                require(children.size == 2) { "Two parameters expected for assignment"}
                val target = (children.first() as Literal).value as String
                (parameterContext as LocalContext).symbols[target] = children.last().value.eval(parameterContext)
            }
            "while" -> {
                require(children.size == 2) { "Two parameters expected for 'while'."}
                while (children[0].value.evalBoolean(parameterContext)) children[1].value.eval(parameterContext)
            }
            else -> throw IllegalStateException("Unrecognized symbol: $name")
        }

    fun evalIf(children: List<Parameter>, parameterContext: RuntimeContext): Any {
        if (children[0].value.evalBoolean(parameterContext)) {
            return children[1].value.eval(parameterContext)
        }
        for (i in 2 until children.size) {
            val child = children[i]
            val value = child.value
            when (child.name) {
                "elif" -> {
                    require (value is Symbol && value.name == "pair")
                    if (value.children[0].value.evalBoolean(parameterContext)) {
                        return value.children[1].value.eval(parameterContext)
                    }
                }
                "else" -> {
                    return value.eval(parameterContext)
                }
                else -> throw IllegalStateException("else or elif expected; got: '${child.name}'")
            }
        }
        return Unit
    }

    fun evalFor(children: List<Parameter>, parameterContext: RuntimeContext) {
        val range = children[0].value.eval(parameterContext) as Collection<Any>
        for (value in range) {
            (children[1].value as Lambda).eval(listOf(Parameter("", Literal(value))), parameterContext)
        }
    }
}