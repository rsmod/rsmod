package gg.rsmod.game.model.client

import gg.rsmod.game.model.map.Coordinates

sealed class Entity(
    var index: Int = -1,
    var coords: Coordinates = Coordinates.ZERO
)

class PlayerEntity(
    val username: String,
    val privilege: Int
) : Entity()

class NpcEntity : Entity()
