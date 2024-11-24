package org.kobjects.sugarcoat.model

/**
 * The compilation passes performed on the model and AST after initial parsing.
 */
enum class ResolutionPass {
    /** Resolve types on signatures and insert methods implied by properties.  */
    SIGNATURES,

    /** Collect a map of pairs of traits and structs to the corresponding implementations */
    IMPLS,

    /** Resolve static fields. */
    STATIC_FIELDS,

    /** Resolve expressions inside functions and default expressions. */
    EXPRESSIONS,
}