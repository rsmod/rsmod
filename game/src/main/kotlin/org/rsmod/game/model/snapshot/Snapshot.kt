package org.rsmod.game.model.snapshot

import org.rsmod.game.model.client.PlayerEntity
import org.rsmod.game.model.item.container.ItemContainerMap
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.stat.StatMap
import org.rsmod.game.model.vars.VarpMap
import java.time.LocalDateTime

data class Snapshot(
    val timestamp: LocalDateTime,
    val coords: Coordinates,
    val entity: PlayerEntity,
    val stats: StatMap,
    val varps: VarpMap,
    val containers: ItemContainerMap
) {

    companion object {

        val INITIAL = Snapshot(
            timestamp = LocalDateTime.now(),
            entity = PlayerEntity(username = "", privilege = 0),
            coords = Coordinates.ZERO,
            stats = StatMap(),
            varps = VarpMap(),
            containers = ItemContainerMap()
        )
    }
}
