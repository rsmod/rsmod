package org.rsmod.plugins.cache.packer.type

import org.rsmod.plugins.api.cache.type.varbit.VarbitTypeList
import org.rsmod.plugins.cache.config.varbit.VarbitType
import org.rsmod.plugins.cache.config.varbit.VarbitTypeBuilder
import org.rsmod.plugins.cache.packer.StringUtil.stripTag
import org.rsmod.plugins.types.NamedTypeMapHolder
import org.rsmod.plugins.types.NamedVarbit
import org.rsmod.plugins.types.NamedVarp

public data class ConfigVarbit(
    val id: Int,
    val name: String,
    val varp: String,
    val lsb: Int,
    val msb: Int,
    val inherit: String?,
    val transmit: Boolean
) {

    public fun toCacheType(
        names: NamedTypeMapHolder,
        types: VarbitTypeList
    ): VarbitType {
        val builder = VarbitTypeBuilder()
        val varp = names.varps.getOrThrow(varp.stripTag())
        builder.id = id
        builder.name = name
        builder.varp = varp.id
        builder.lsb = lsb
        builder.msb = msb
        // TODO: should we set this implicitly through varp.transmit?
        builder.transmit = transmit
        inherit?.let {
            val named = names.varbits.getOrThrow(it.stripTag())
            builder += types.getValue(named.id)
        }
        return builder.build()
    }

    private companion object {

        private fun Map<String, NamedVarbit>.getOrThrow(name: String): NamedVarbit =
            this[name] ?: error("Varbit with name `$name` not found in cache.")

        private fun Map<String, NamedVarp>.getOrThrow(name: String): NamedVarp =
            this[name] ?: error("Varp with name `$name` not found in cache.")
    }
}
