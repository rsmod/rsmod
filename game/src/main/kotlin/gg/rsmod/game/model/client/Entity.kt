package gg.rsmod.game.model.client

import gg.rsmod.game.model.map.Coordinates
import gg.rsmod.game.update.mask.UpdateMaskSet

sealed class Entity(
    var index: Int = -1,
    var coords: Coordinates = Coordinates.ZERO,
    val updates: UpdateMaskSet = UpdateMaskSet()
)

class PlayerEntity(
    val username: String,
    val rank: Int
) : Entity()

class NpcEntity : Entity()
