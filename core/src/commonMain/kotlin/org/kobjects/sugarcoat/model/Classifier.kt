package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.type.Type
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
    val staticFields = mutableMapOf<String, StaticFieldDefinition>()

    val program: Program
        get() = parent?.program ?: (this as Program)

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

    open fun addStaticField(mutable: Boolean, name: String, type: Type?, initializer: Expression) {
        staticFields[name] = StaticFieldDefinition(name, type, initializer)
    }

    open fun addInstanceField(mutable: Boolean, name: String, type: Type, initializer: Expression?) {
        throw UnsupportedOperationException("Instance Fields are not supported for ${this::class}")
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

    /* Insert methods implied by fields */
    open fun resolveImpliedMethods() {}

    /** Resolve types on signatures  */
    open fun resolveSignatures() {}

    /** Register all impls with the program impl registry. */
    open fun resolveImpls(program: Program) {}

    /** Resolve function bodies */
    open fun resolveExpressions() {}

    fun resolutionPass(program: Program, pass: ResolutionPass) {
        when (pass) {
            ResolutionPass.IMPLIED_METHODS -> resolveImpliedMethods()
            ResolutionPass.SIGNATURES -> resolveSignatures()
            ResolutionPass.IMPLS -> resolveImpls(program)
            ResolutionPass.EXPRESSIONS -> resolveExpressions()
        }
        for (definition in definitions.values) {
            definition.resolutionPass(program, pass)
        }
        for (definition in unnamed) {
            definition.resolutionPass(program, pass)
        }
    }


    fun resolveSymbolOrNull(name: String): Classifier? {
        val result = definitions[name]
        if (result != null) {
            return result
        }
        val fb = fallback
        if (fb == null) {
            return null
        }
        return fb.resolveSymbolOrNull(name)
    }


    abstract fun serialize(sb: StringBuilder)

    fun resolveSymbol(name: String, errorPrefix: (() -> String) = { "(Unknown location)" }): Classifier {
        val result = resolveSymbolOrNull(name)
        require (result != null) {
            "${errorPrefix()}: Unable to resolve '$name' in\n${dump()}"
        }
        return result
    }

    fun dump(): String {
        val result = "${if (name.isEmpty()) "(${this::class.simpleName})" else name}: ${definitions.keys}"
        val fallback = fallback
        return if (fallback == null) result else result + "\n" + fallback.dump()
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


    /**
     * For the definitions of the passes, please refer to the corresponding method documentations
     * in Classifier.
     */
    enum class ResolutionPass {
        SIGNATURES,
        IMPLIED_METHODS,
        IMPLS,
        EXPRESSIONS,
    }
}