package org.kobjects.sugarcoat


interface RuntimeContext {
    fun evalSymbol(name: String, children: List<Parameter>, parameterContext: RuntimeContext): Any

    fun numeric1(children: List<Parameter>, f: (Double) -> Any): Any {
        require(children.size == 1)
        return f(children[0].value.evalDouble(this))
    }

    fun numeric2(children: List<Parameter>, f: (Double, Double) -> Any) : Any {
        require(children.size == 2)
        return f(children[0].value.evalDouble(this), children[1].value.evalDouble(this))
    }
}