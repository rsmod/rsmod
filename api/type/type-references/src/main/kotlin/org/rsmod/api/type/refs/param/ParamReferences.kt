package org.rsmod.api.type.refs.param

import kotlin.reflect.KClass
import org.rsmod.api.type.refs.TypeReferences
import org.rsmod.game.type.param.HashedParamType
import org.rsmod.game.type.param.ParamType

public abstract class ParamReferences :
    TypeReferences<ParamType<*>, Nothing>(ParamType::class.java) {
    /**
     * Find the respective type based on their [internal] name.
     *
     * @param internal the internal name as mapped through name symbols.
     * @param hash can be provided as a verification method to ensure the type's identity hash
     *   matches an expected value generated from the type's `computeIdentityHash` function.
     * @see [org.rsmod.api.type.symbols.name.NameMapping]
     * @see [org.rsmod.api.type.symbols.name.NameLoader]
     */
    public inline fun <reified T : Any> find(
        internal: String,
        hash: Long? = null,
    ): HashedParamType<T> = find(T::class, internal, hash)

    /**
     * Find the respective type based on their [internal] name.
     *
     * @param internal the internal name as mapped through name symbols.
     * @param hash can be provided as a verification method to ensure the type's identity hash
     *   matches an expected value generated from the type's `computeIdentityHash` function.
     * @see [org.rsmod.api.type.symbols.name.NameMapping]
     * @see [org.rsmod.api.type.symbols.name.NameLoader]
     */
    public fun <T : Any> find(
        type: KClass<T>,
        internal: String,
        hash: Long? = null,
    ): HashedParamType<T> {
        val paramType =
            HashedParamType(
                type = type,
                startHash = hash,
                typedDefault = null,
                internalName = internal,
            )
        cache += paramType
        return paramType
    }
}
