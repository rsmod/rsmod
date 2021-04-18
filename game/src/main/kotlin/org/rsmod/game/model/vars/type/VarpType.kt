package org.rsmod.game.model.vars.type

import org.rsmod.game.cache.type.ConfigType
import org.rsmod.game.cache.type.CacheTypeList

data class VarpType(
    override val id: Int,
    val type: Int
) : ConfigType

class VarpTypeList : CacheTypeList<VarpType>()
