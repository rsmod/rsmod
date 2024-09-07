package org.rsmod.game.type.util

import org.rsmod.game.type.literal.CacheVarTypeMap
import org.rsmod.game.type.param.ParamType

@JvmInline
public value class ParamMapBuilder(private val typed: MutableMap<Int, Any> = hashMapOf()) {
    public fun toParamMap(): ParamMap {
        val primitive =
            typed.entries.associate {
                val codec = CacheVarTypeMap.findCodec<Any, Any>(it.value::class)
                val primitive = codec.encode(it.value)
                it.key to primitive
            }
        return ParamMap(primitive, typed)
    }

    public fun isNotEmpty(): Boolean = typed.isNotEmpty()

    public operator fun <T : Any> set(param: ParamType<T>, value: T) {
        typed[param.id] = value
    }
}
