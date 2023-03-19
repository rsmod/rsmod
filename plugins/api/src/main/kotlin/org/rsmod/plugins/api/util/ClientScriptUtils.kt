package org.rsmod.plugins.api.util

import org.rsmod.plugins.api.cache.type.literal.CacheTypeLiteral
import org.rsmod.plugins.api.cache.type.literal.codec.CacheTypeCodec

public object ClientScriptUtils {

    @Suppress("UNCHECKED_CAST")
    public fun buildArgs(args: List<Any>): RunClientScriptArgs {
        val typeChars = CharArray(args.size)
        val converted = List(args.size) { index ->
            val argIndex = args.size - 1 - index
            val arg = args[argIndex]
            val type = CacheTypeLiteral.mappedOutClasses[arg::class]
                ?: error("Could not find cache-type literal for type ${arg::class}. ($arg)")
            val codec = type.codec as CacheTypeCodec<Any, Any>
            val value = codec.encode(arg)
            typeChars[argIndex] = type.char
            return@List value
        }
        return RunClientScriptArgs(String(typeChars), converted)
    }

    public data class RunClientScriptArgs(
        val typeChars: String,
        val converted: List<Any>
    ) : List<Any> by converted
}
