package org.rsmod.plugins.api.spawn

import org.rsmod.game.model.domain.Direction

data class NpcSpawnConfig(
    val id: Int,
    val x: Int,
    val y: Int,
    val level: Int,
    val wander: Int,
    val face: Direction
)
