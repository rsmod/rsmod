package org.rsmod.plugins.cache.config.param

import org.rsmod.plugins.cache.config.ConfigType
import org.rsmod.plugins.cache.literal.CacheTypeLiteral

public data class ParamType<T>(
    override val id: Int,
    val name: String?,
    val transmit: Boolean,
    val type: CacheTypeLiteral?,
    val autoDisable: Boolean,
    val default: T?
) : ConfigType
