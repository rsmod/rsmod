package org.rsmod.game.model.vars.type

import org.rsmod.game.cache.ConfigType
import org.rsmod.game.cache.ConfigTypeList

data class VarpType(
    override val id: Int,
    val type: Int
) : ConfigType

class VarpTypeList : ConfigTypeList<VarpType>()
