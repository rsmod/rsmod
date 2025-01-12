package org.rsmod.api.cache.util

import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import org.rsmod.game.type.TypeListMap
import org.rsmod.game.type.TypeResolver
import org.rsmod.game.type.literal.BaseCacheVarType
import org.rsmod.game.type.literal.CacheVarCodec
import org.rsmod.game.type.literal.CacheVarIntCodec
import org.rsmod.game.type.literal.CacheVarStringCodec
import org.rsmod.game.type.literal.CacheVarTypeMap
import org.rsmod.game.type.literal.CacheVarTypeMap.codecOut
import org.rsmod.game.type.param.ParamType
import org.rsmod.game.type.util.ParamMap

public object ComplexTypeDecoder {
    public fun decodeAll(typeLists: TypeListMap) {
        typeLists.resolveParamDefaults()
        typeLists.resolveEnumEntries()
        typeLists.resolveParamMaps()
    }

    @Suppress("UNCHECKED_CAST")
    private fun TypeListMap.resolveParamDefaults() {
        val typed = params.filter { it.value.default != null }.values
        for (type in typed) {
            val literal = type.typeLiteral ?: continue
            val complex = CacheVarTypeMap.codecMap[literal.codecOut]
            val primitive: Any =
                when (literal.type) {
                    BaseCacheVarType.Integer -> {
                        val codec = CacheVarIntCodec
                        val primitive =
                            checkNotNull(type.defaultInt) {
                                "`defaultInt` should not be null when `typeLiteral`" +
                                    " is defined. (type=$type)"
                            }
                        codec.decode(this, primitive)
                    }
                    BaseCacheVarType.String -> {
                        val codec = CacheVarStringCodec
                        val primitive =
                            checkNotNull(type.defaultStr) {
                                "`defaultStr` should not be null when `typeLiteral` " +
                                    "is defined. (type=$type)"
                            }
                        codec.decode(this, primitive)
                    }
                }
            if (complex != null) {
                val codec = complex as CacheVarCodec<Any, Any>
                val resolved = codec.decode(this, primitive)
                val rawType = type as ParamType<Any>
                TypeResolver.setDefault(rawType, resolved)
                continue
            }
            val resolved =
                when (literal.type) {
                    BaseCacheVarType.Integer -> {
                        val codec = CacheVarIntCodec
                        // Should always be an Int at this point in the code,
                        // can't think of a situation where it's not the case.
                        check(primitive is Int)
                        codec.decode(this, primitive)
                    }
                    BaseCacheVarType.String -> {
                        val codec = CacheVarStringCodec
                        // Should always be a String at this point in the code,
                        // can't think of a situation where it's not the case.
                        check(primitive is String)
                        codec.decode(this, primitive)
                    }
                }
            val rawType = type as ParamType<Any>
            TypeResolver.setDefault(rawType, resolved)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun TypeListMap.resolveEnumEntries() {
        val types = enums.filter { it.value.primitiveMap.isNotEmpty() }.values
        for (type in types) {
            val keyLiteral = type.keyLiteral
            val valLiteral = type.valLiteral
            val keyOut = keyLiteral.codecOut
            val valOut = valLiteral.codecOut
            val keyCodec = CacheVarTypeMap.codecMap[keyOut]
            val valCodec = CacheVarTypeMap.codecMap[valOut]
            // Resolve default value if required.
            var defaultValue: Any? = type.defaultStr ?: type.defaultInt
            if (defaultValue != null) {
                val codec = valCodec as CacheVarCodec<Any, Any>
                val resolved = codec.decode(this, defaultValue)
                defaultValue = resolved
            }
            TypeResolver.setDefault(type, defaultValue)
            // Resolve key <-> value map.
            val resolvedMap = mutableMapOf<Any, Any?>()
            for ((rawKey, rawVal) in type.primitiveMap) {
                var resolvedKey: Any = rawKey
                var resolvedVal: Any? = rawVal
                if (keyCodec != null) {
                    val codec = keyCodec as CacheVarCodec<Any, Any>
                    val resolved =
                        checkNotNull(codec.decode(this, rawKey)) {
                            "Could not decode key `$rawKey` with codec for " +
                                "literal $keyLiteral. (codec=$keyCodec, enum=$type)"
                        }
                    resolvedKey = resolved
                }
                if (valCodec != null && rawVal != null) {
                    val codec = valCodec as CacheVarCodec<Any, Any>
                    val resolved = codec.decode(this, rawVal) ?: defaultValue
                    resolvedVal = resolved
                }
                resolvedMap[resolvedKey] = resolvedVal
            }
            TypeResolver.setTypedMap(type, resolvedMap)
        }
    }

    private fun TypeListMap.resolveParamMaps() {
        val locs = locs.values.mapNotNull { it.paramMap }
        val npcs = npcs.values.mapNotNull { it.paramMap }
        val objs = objs.values.mapNotNull { it.paramMap }
        val structs = structs.values.mapNotNull { it.paramMap }
        val paramsList = locs + npcs + objs + structs
        for (params in paramsList) {
            params.resolveTypedMap(this)
        }
    }

    private fun ParamMap.resolveTypedMap(cacheTypes: TypeListMap) {
        val typedMap = hashMapOf<Int, Any?>()
        for ((key, primitive) in primitiveMap) {
            val param = cacheTypes.params[key] ?: continue
            val literal =
                param.typeLiteral ?: error("ParamType requires a type to be used for maps: $param")
            val codec = CacheVarTypeMap.findCodec<Any, Any>(literal)
            val typedValue = codec.decode(cacheTypes, primitive)
            typedMap[key] = typedValue
        }
        this.typedMap = typedMap
    }
}
