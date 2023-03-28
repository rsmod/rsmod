package org.rsmod.plugins.api.config.type

import org.rsmod.plugins.api.cache.type.param.ParamTypeList
import org.rsmod.plugins.api.config.CacheTypeLiteralUtil.TYPE_STRING_CONVERSION
import org.rsmod.plugins.api.config.CacheTypeLiteralUtil.convert
import org.rsmod.plugins.api.config.StringUtil.stripTag
import org.rsmod.plugins.cache.config.param.ParamType
import org.rsmod.plugins.cache.config.param.ParamTypeBuilder
import org.rsmod.plugins.types.NamedParameter
import org.rsmod.plugins.types.NamedTypeMapHolder

public data class ConfigParam(
    val id: Int,
    val name: String,
    val transmit: Boolean,
    val inherit: String?,
    val type: String?,
    val default: Any?,
    val autoDisable: Boolean = true
) {

    @Suppress("UNCHECKED_CAST")
    public fun toCacheType(
        names: NamedTypeMapHolder,
        types: ParamTypeList
    ): ParamType<*> {
        val builder = ParamTypeBuilder()
        val typeId = TYPE_STRING_CONVERSION[type]
        val default = default
        builder.id = id
        builder.name = name
        builder.transmit = transmit
        builder.typeChar = typeId?.char
        builder.autoDisable = autoDisable
        if (typeId == null) {
            if (default is String) {
                builder.defaultStr = default
            } else if (default is Int) {
                builder.defaultInt = default
            }
        } else if (default != null) {
            val converted = default.convert(names, typeId)
            if (typeId.isString) {
                builder.defaultStr = typeId.encodeString(converted)
            } else if (typeId.isInt) {
                builder.defaultInt = typeId.encodeInt(converted)
            }
        }
        inherit?.let {
            val named = names.parameters.getOrThrow(it.stripTag())
            builder += types.getValue(named.id)
        }
        return builder.build()
    }

    private companion object {

        private fun Map<String, NamedParameter<*>>.getOrThrow(name: String): NamedParameter<*> =
            this[name] ?: error("Param with name `$name` not found in cache.")
    }
}
