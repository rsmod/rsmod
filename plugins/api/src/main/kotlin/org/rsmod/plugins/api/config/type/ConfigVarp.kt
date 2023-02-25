package org.rsmod.plugins.api.config.type

public data class ConfigVarp(
    val id: Int,
    val alias: String,
    val transmit: Boolean,
    val clientCode: Int?,
    val inherit: String?
)
