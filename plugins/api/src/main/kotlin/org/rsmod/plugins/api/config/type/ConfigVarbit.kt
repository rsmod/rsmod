package org.rsmod.plugins.api.config.type

public data class ConfigVarbit(
    val id: Int,
    val alias: String,
    val varp: String,
    val lsb: Int,
    val msb: Int,
    val inherit: String?,
    val transmit: Boolean
)
