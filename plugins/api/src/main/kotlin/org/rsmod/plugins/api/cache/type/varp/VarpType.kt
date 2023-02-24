package org.rsmod.plugins.api.cache.type.varp

import org.rsmod.plugins.api.cache.type.ConfigType

public data class VarpType(
    override val id: Int,
    public val alias: String?,
    public val clientCode: Int?,
    public val transmit: Boolean
) : ConfigType {

    public companion object {

        public const val TRANSMISSION_OPCODE: Int = 230
    }
}
