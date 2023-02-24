package org.rsmod.plugins.api.cache.type.varbit

import org.rsmod.plugins.api.cache.type.ConfigType

public data class VarbitType(
    override val id: Int,
    public val alias: String?,
    public val varp: Int,
    public val lsb: Int,
    public val msb: Int,
    public val transmit: Boolean
) : ConfigType
