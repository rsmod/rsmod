package org.rsmod.plugins.api.cache.config.npc

import org.rsmod.game.model.npc.type.NpcTypeBuilder

data class NpcConfig(
    val id: Int,
    val inherit: Int?,
    val dataFile: String?,
    val pack: Boolean,
    val builder: NpcTypeBuilder
)
