package org.rsmod.game.model.vars.type

import org.rsmod.game.cache.type.ConfigType
import org.rsmod.game.cache.type.ConfigTypeList

data class VarpType(
    override val id: Int,
    val type: Int
) : ConfigType

class VarpTypeList : ConfigTypeList<VarpType>()
