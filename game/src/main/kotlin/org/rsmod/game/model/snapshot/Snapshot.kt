package org.rsmod.game.model.snapshot

import org.rsmod.game.model.map.Coordinates
import java.time.LocalDateTime

data class Snapshot(
    val timestamp: LocalDateTime,
    val coords: Coordinates
) {

    companion object {

        val INITIAL = Snapshot(
            timestamp = LocalDateTime.now(),
            coords = Coordinates.ZERO
        )
    }
}
