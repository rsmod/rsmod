package org.rsmod.plugins.api.cache.type.param

import org.rsmod.plugins.api.cache.type.ConfigType
import org.rsmod.plugins.api.cache.type.literal.CacheTypeLiteral

public data class ParamType<T>(
    override val id: Int,
    val name: String?,
    val transmit: Boolean,
    val type: CacheTypeLiteral?,
    val autoDisable: Boolean,
    val default: T?
) : ConfigType
