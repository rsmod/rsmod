package org.rsmod.game.model.client

import org.rsmod.game.model.domain.Appearance
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.update.mask.UpdateMaskSet

sealed class Entity(
    var index: Int = -1,
    var coords: Coordinates = Coordinates.ZERO,
    val updates: UpdateMaskSet = UpdateMaskSet()
)

class PlayerEntity(
    val username: String,
    val privilege: Int,
    var appearance: Appearance = Appearance.ZERO
) : Entity() {

    fun copy() = PlayerEntity(
        username,
        privilege,
        appearance.copy()
    )

    companion object {

        val ZERO = PlayerEntity("", 0)
    }
}

class NpcEntity(
    val invisible: Boolean = false,
    val transform: Int = -1
) : Entity()
