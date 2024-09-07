package org.rsmod.api.type.refs.enums

import kotlin.reflect.KClass
import org.rsmod.api.type.refs.TypeReferences
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.enums.HashedEnumType

public abstract class EnumReferences :
    TypeReferences<EnumType<*, *>, Nothing>(EnumType::class.java) {
    public inline fun <reified K : Any, reified V : Any> find(hash: Long): EnumType<K, V> =
        find(K::class, V::class, hash)

    public fun <K : Any, V : Any> find(
        key: KClass<K>,
        value: KClass<V>,
        hash: Long,
    ): EnumType<K, V> {
        val type = HashedEnumType(key, value, hash)
        cache += type
        return type
    }
}
