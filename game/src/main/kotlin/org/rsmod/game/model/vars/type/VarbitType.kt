package org.rsmod.game.model.vars.type

import org.rsmod.game.cache.type.ConfigType
import org.rsmod.game.cache.type.ConfigTypeList

data class VarbitType(
    override val id: Int,
    val varp: Int,
    val lsb: Int,
    val msb: Int
) : ConfigType

class VarbitTypeList : ConfigTypeList<VarbitType>()
