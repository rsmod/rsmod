package org.rsmod.api.type.refs.enums

import kotlin.reflect.KClass
import org.rsmod.api.type.refs.TypeReferences
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.enums.HashedEnumType

public abstract class EnumReferences :
    TypeReferences<EnumType<*, *>, Nothing>(EnumType::class.java) {
    /**
     * Find the respective type based on their [internal] name.
     *
     * @param internal the internal name as mapped through name symbols.
     * @param hash can be provided as a verification method to ensure the type's identity hash
     *   matches an expected value generated from the type's `computeIdentityHash` function.
     * @see [org.rsmod.api.type.symbols.name.NameMapping]
     * @see [org.rsmod.api.type.symbols.name.NameLoader]
     */
    public inline fun <reified K : Any, reified V : Any> find(
        internal: String,
        hash: Long? = null,
    ): EnumType<K, V> = find(K::class, V::class, internal, hash)

    /**
     * Find the respective type based on their [internal] name.
     *
     * @param internal the internal name as mapped through name symbols.
     * @param hash can be provided as a verification method to ensure the type's identity hash
     *   matches an expected value generated from the type's `computeIdentityHash` function.
     * @see [org.rsmod.api.type.symbols.name.NameMapping]
     * @see [org.rsmod.api.type.symbols.name.NameLoader]
     */
    public fun <K : Any, V : Any> find(
        key: KClass<K>,
        value: KClass<V>,
        internal: String,
        hash: Long? = null,
    ): EnumType<K, V> {
        val type = HashedEnumType(key, value, hash, internalName = internal)
        cache += type
        return type
    }
}
