package org.rsmod.plugins.cache.config.varbit

import org.rsmod.plugins.cache.config.ConfigType

public data class VarbitType(
    override val id: Int,
    public val name: String?,
    public val varp: Int,
    public val lsb: Int,
    public val msb: Int,
    public val transmit: Boolean
) : ConfigType
