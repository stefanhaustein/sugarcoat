package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ParameterReference
import org.kobjects.sugarcoat.base.Type
import org.kobjects.sugarcoat.datatype.NativeArgList
import org.kobjects.sugarcoat.datatype.NativeFunction
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.fn.TypedCallable

abstract class Classifier(
    open val parent: Classifier?,
    open val name: String,
    open val fallback: Classifier? = null
) {
    val definitions = mutableMapOf<String, Classifier>()
    val unnamed = mutableListOf<Classifier>()

    open fun selfType(): Type = throw UnsupportedOperationException()

    open fun addChild(value: Classifier) {
        require(value.parent == this)
        if (value.name.isEmpty()) {
            unnamed.add(value)
        } else {
            require(!definitions.contains(value.name)) { "Symbol defined already: '${value.name}'" }
            definitions[value.name] = value
        }
    }

    open fun addField(name: String, type: Type?, defaultExpression: Expression?) {
        throw UnsupportedOperationException("Fields are not supported for ${this::class}")
    }


    fun addNativeMethod(
        returnType: Type,
        name: String,
        vararg args: ParameterDefinition,
        op: (NativeArgList) -> Any
    ) {
        require (this is Type) {
            "Methods can only be added to types."
        }
        addChild(NativeFunction(this, false, returnType, name, args.toList(), op))
    }

    fun addNativeFunction(
        returnType: Type,
        name: String,
        vararg args: ParameterDefinition,
        op: (NativeArgList) -> Any
    ) {
        addChild(NativeFunction(this, true, returnType, name, args.toList(), op))
    }


    fun addControl(name: String, returnType: Type, vararg parameters: ParameterDefinition, action: (List<Expression?>, LocalRuntimeContext) -> Any) {

        addChild(object : TypedCallable, Classifier(this, name, null) {
            override val static: Boolean
                get() = true

            override fun call(
                receiver: Any?,
                children: List<Expression?>,
                parameterScope: LocalRuntimeContext
            ): Any {
                return action(children, parameterScope)
            }

            override fun serialize(sb: StringBuilder) {
                throw UnsupportedOperationException()
            }

            override val type: FunctionType
                get() = FunctionType(returnType, parameters.toList())

            override fun toString() = "control instruction '$name'"

        })
    }

    open fun resolveTypes() {
        for (definition in definitions.values) {
            definition.resolveTypes()
        }
        for (definition in unnamed) {
            definition.resolveTypes()
        }
    }

    open fun resolveExpressions() {
        for (definition in definitions.values) {
            definition.resolveExpressions()
        }
        for (definition in unnamed) {
            definition.resolveExpressions()
        }
    }

    fun resolveOrNull(name: String): Classifier? {
        val result = definitions[name]
        if (result != null) {
            return result
        }
        val fb = fallback
        if (fb == null) {
            return null
        }
        return fb.resolveOrNull(name)
    }


    abstract fun serialize(sb: StringBuilder)

    open fun resolve(name: String): Classifier {
        val result = resolveOrNull(name)
        if (result != null) {
            return result
        }
        if (fallback != null) {
            try {
                fallback!!.resolve(name)
            } catch (e: Exception) {
                throw IllegalStateException("Unable to resolve '$name' in '${this.name}' containing ${definitions.keys}", e)
            }
        }
        throw IllegalStateException("Unable to resolve '$name' in '${this.name}' containing ${definitions.keys}")
    }

    abstract override fun toString(): String

    fun serializeBody(sb: StringBuilder) {
        for ((name, definition) in definitions) {
            sb.append("  ")
            definition.serialize(sb)
        }

    }

    fun collectImpls(impls: MutableList<ImplDefinition>) {
        if (this is ImplDefinition) {
            impls.add(this)
        }
        for ((_, classifier) in definitions) {
            classifier.collectImpls(impls)
        }
        for (classifier in unnamed) {
            classifier.collectImpls(impls)
        }
    }

    /*=
        buildString {
            for ((name, definition) in definitions) {
                when (definition) {
                    is FunctionDefinition -> {
                        append("fn $name$definition\n")
                    }
                }
            }
        }
*/
}