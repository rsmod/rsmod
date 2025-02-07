package org.rsmod.api.repo.world

import jakarta.inject.Inject
import org.rsmod.api.registry.zone.ZoneUpdateMap
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.type.synth.SynthType
import org.rsmod.map.CoordGrid

public class WorldRepository @Inject constructor(private val zoneUpdates: ZoneUpdateMap) {
    public fun soundArea(
        source: CoordGrid,
        synth: SynthType,
        delay: Int = 0,
        loops: Int = 1,
        radius: Int = 5,
        size: Int = 0,
    ) {
        zoneUpdates.soundArea(source, synth.id, delay, loops, radius, size)
    }

    public fun soundArea(
        source: PathingEntity,
        synth: SynthType,
        delay: Int = 0,
        loops: Int = 1,
        radius: Int = 5,
    ) {
        soundArea(source.coords, synth, delay, loops, radius, source.size)
    }
}
