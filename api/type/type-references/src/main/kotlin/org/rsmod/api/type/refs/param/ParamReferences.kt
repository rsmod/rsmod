package org.rsmod.api.type.refs.param

import kotlin.reflect.KClass
import org.rsmod.api.type.refs.TypeReferences
import org.rsmod.game.type.param.HashedParamType
import org.rsmod.game.type.param.ParamType

public abstract class ParamReferences :
    TypeReferences<ParamType<*>, Nothing>(ParamType::class.java) {
    public inline fun <reified T : Any> find(hash: Long): HashedParamType<T> = find(T::class, hash)

    public fun <T : Any> find(type: KClass<T>, hash: Long): HashedParamType<T> {
        val type = HashedParamType(type, startHash = hash, typedDefault = null)
        cache += type
        return type
    }
}
