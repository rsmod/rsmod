package org.rsmod.plugins.cache.packer.type

import org.rsmod.plugins.api.cache.type.varp.VarpTypeList
import org.rsmod.plugins.cache.config.varp.VarpType
import org.rsmod.plugins.cache.config.varp.VarpTypeBuilder
import org.rsmod.plugins.cache.packer.util.StringUtils.stripTag
import org.rsmod.plugins.types.NamedTypeMapHolder
import org.rsmod.plugins.types.NamedVarp

public data class ConfigVarp(
    val id: Int,
    val name: String,
    val transmit: Boolean,
    val persist: Boolean,
    val clientCode: Int?,
    val inherit: String?
) {

    public fun toCacheType(
        names: NamedTypeMapHolder,
        types: VarpTypeList
    ): VarpType {
        val builder = VarpTypeBuilder()
        builder.id = id
        builder.name = name
        builder.clientCode = clientCode
        builder.transmit = transmit
        builder.persist = persist
        inherit?.let {
            val named = names.varps.getOrThrow(it.stripTag())
            builder += types.getValue(named.id)
        }
        return builder.build()
    }

    private companion object {

        private fun Map<String, NamedVarp>.getOrThrow(name: String): NamedVarp =
            this[name] ?: error("Varp with name `$name` not found in cache.")
    }
}
