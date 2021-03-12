package org.rsmod.game.model.snapshot

import org.rsmod.game.model.item.container.ItemContainerMap
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.stat.StatMap
import org.rsmod.game.model.vars.VarpMap
import java.time.LocalDateTime

data class Snapshot(
    val timestamp: LocalDateTime,
    val coords: Coordinates,
    val stats: StatMap,
    val varps: VarpMap,
    val containers: ItemContainerMap
) {

    companion object {

        val INITIAL = Snapshot(
            timestamp = LocalDateTime.now(),
            coords = Coordinates.ZERO,
            stats = StatMap(),
            varps = VarpMap(),
            containers = ItemContainerMap()
        )
    }
}
