package org.kobjects.sugarcoat.model

import org.kobjects.sugarcoat.ast.Expression
import org.kobjects.sugarcoat.ast.ResolutionContext
import org.kobjects.sugarcoat.type.Type
import org.kobjects.sugarcoat.datatype.NativeArgList
import org.kobjects.sugarcoat.datatype.NativeFunction
import org.kobjects.sugarcoat.datatype.VoidType
import org.kobjects.sugarcoat.fn.FunctionType
import org.kobjects.sugarcoat.fn.LocalRuntimeContext
import org.kobjects.sugarcoat.fn.ParameterDefinition
import org.kobjects.sugarcoat.fn.Callable

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

    open fun resolveGenericParameters(resolvedTypes: List<Type>): Type {
        require(resolvedTypes.isEmpty()) {
            "$this does not support generic type parameters"
        }
        require(this is Type) {
            "$this is not a type"
        }
        return this
    }

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
        staticFields[name] = StaticFieldDefinition(this, mutable, name, type, initializer)
    }

    open fun addInstanceField(mutable: Boolean, name: String, type: Type, initializer: Expression?) {
        throw UnsupportedOperationException("Instance Fields are not supported for ${this::class}")
    }

    fun initialize(globalRuntimeContext: GlobalRuntimeContext) {
        for (field in staticFields.values) {
            globalRuntimeContext.symbols[field] = field.initializer.eval(LocalRuntimeContext(globalRuntimeContext, null))
        }
        for (child in definitions.values + unnamed) {
            child.initialize(globalRuntimeContext)
        }
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

        addChild(object : Callable, Classifier(this, name, null) {
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
    
    open fun resolveSignatures() {}

    open fun resolveImpls(program: Program) {}

    open fun resolveExpressions() {}

    open fun resolveStaticFields() {
        val resolutionContext = ResolutionContext(this)
        for (field in staticFields.values) {
            val resolvedExplicitType = field.explicitType?.resolve(this)
            field.initializer = field.initializer.resolve(resolutionContext, resolvedExplicitType)
            val resolvedType = resolvedExplicitType ?: field.initializer.getType()
            addControl(field.name, resolvedType) { param, localContext ->
                localContext.globalRuntimeContext.symbols[field]!!
            }
            if (field.mutable) {
                addControl(
                    field.name,
                    VoidType,
                    ParameterDefinition("value", resolvedType)
                ) { params, localRuntimeContext ->
                    localRuntimeContext.globalRuntimeContext.symbols[field] = params[0]!!.eval(localRuntimeContext)
                    Unit
                }
            }
        }
    }

    fun resolutionPass(program: Program, pass: ResolutionPass) {
        when (pass) {
            ResolutionPass.SIGNATURES -> resolveSignatures()
            ResolutionPass.STATIC_FIELDS -> resolveStaticFields()
            ResolutionPass.IMPLS -> resolveImpls(program)
            ResolutionPass.EXPRESSIONS -> resolveExpressions()
        }
        for (definition in definitions.values + unnamed) {
            definition.resolutionPass(program, pass)
        }
    }


    fun resolveSymbolOrNull(name: String): Classifier? {
        val result = definitions[name]
        if (result != null) {
            return result
        }
        val fb = fallback
        return fb?.resolveSymbolOrNull(name)
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


}