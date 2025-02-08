package org.rsmod.api.type.refs

public abstract class TypeReferences<T, I>(internal val type: Class<T>) {
    internal val cache = mutableListOf<T>()
}

public abstract class HashTypeReferences<T>(type: Class<T>) : TypeReferences<T, Long>(type) {
    /**
     * Find the respective type based on their [internal] name.
     *
     * @param internal the internal name as mapped through name symbols.
     * @param hash can be provided as a verification method to ensure the type's identity hash
     *   matches an expected value generated from the type's `computeIdentityHash` function.
     * @see [org.rsmod.api.type.symbols.name.NameMapping]
     * @see [org.rsmod.api.type.symbols.name.NameLoader]
     */
    public abstract fun find(internal: String, hash: Long? = null): T

    /**
     * Verifies that the "identity hash" of the type associated with [internal] matches the expected
     * [hash]. Unlike [find], this function does not return the type itself but ensures that its
     * computed hash is correct.
     *
     * Use this when you only need to confirm the identity of a type without retrieving it.
     */
    public fun verify(internal: String, hash: Long) {
        find(internal, hash)
    }
}

public abstract class NameTypeReferences<T>(type: Class<T>) : TypeReferences<T, Long>(type) {
    /**
     * Find the respective type based on their [internal] name.
     *
     * @param internal the internal name as mapped through name symbols.
     * @see [org.rsmod.api.type.symbols.name.NameMapping]
     * @see [org.rsmod.api.type.symbols.name.NameLoader]
     */
    public abstract fun find(internal: String): T
}
