package org.rsmod.game.model.vars.type

import org.rsmod.game.cache.ConfigType
import org.rsmod.game.cache.ConfigTypeList

data class VarbitType(
    override val id: Int,
    val varp: Int,
    val lsb: Int,
    val msb: Int
) : ConfigType

class VarbitTypeList : ConfigTypeList<VarbitType>()
