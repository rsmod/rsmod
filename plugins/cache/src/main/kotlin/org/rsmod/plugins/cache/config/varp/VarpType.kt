package org.rsmod.plugins.cache.config.varp

import org.rsmod.plugins.cache.config.ConfigType

public data class VarpType(
    override val id: Int,
    public val name: String?,
    public val clientCode: Int?,
    public val transmit: Boolean,
    public val persist: Boolean
) : ConfigType
