package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.api.registry.region.RegionRegistry
import org.rsmod.events.EventBus
import org.rsmod.game.area.AreaIndex
import org.rsmod.game.entity.Player
import org.rsmod.game.queue.EngineQueueCache
import org.rsmod.game.queue.EngineQueueType
import org.rsmod.map.CoordGrid
import org.rsmod.map.square.MapSquareKey
import org.rsmod.map.zone.ZoneKey

public class PlayerMapUpdateProcessor
@Inject
constructor(
    private val eventBus: EventBus,
    private val queueCache: EngineQueueCache,
    private val playerRegistry: PlayerRegistry,
    private val regionRegistry: RegionRegistry,
    private val areaIndex: AreaIndex,
) {
    public fun process(player: Player) {
        player.processMapSquareChange()
        player.processZoneChange()
        player.processAreaChange()
    }

    private fun Player.processMapSquareChange() {
        val currentMapSquare = MapSquareKey.from(coords)
        val previousMapSquare = MapSquareKey.from(previousCoords)
        if (currentMapSquare != previousMapSquare) {
            enterMapSquare(previousMapSquare, currentMapSquare)
        }
    }

    private fun Player.enterMapSquare(previous: MapSquareKey, current: MapSquareKey) {
        if (previous.hasExitScript()) {
            engineQueueMapzoneExit(previous)
        }
        if (current.hasEnterScript()) {
            engineQueueMapzone(current)
        }
    }

    private fun Player.processZoneChange() {
        val currZone = ZoneKey.from(coords)

        val prevProcessedZone = lastProcessedZone
        if (currZone != prevProcessedZone) {
            changeZone(prevProcessedZone, currZone)
        }

        val prevZone = ZoneKey.from(previousCoords)
        if (currZone != prevZone) {
            enterZone(prevZone, currZone)
        }
    }

    private fun Player.changeZone(previous: ZoneKey, current: ZoneKey) {
        playerRegistry.change(this, from = previous, to = current)
    }

    private fun Player.enterZone(previous: ZoneKey, current: ZoneKey) {
        if (previous.hasExitScript()) {
            engineQueueZoneExit(previous)
        }
        if (current.hasEnterScript()) {
            engineQueueZone(current)
        }
    }

    private fun MapSquareKey.hasEnterScript(): Boolean {
        return queueCache.hasScript(EngineQueueType.Mapzone, id)
    }

    private fun MapSquareKey.hasExitScript(): Boolean {
        return queueCache.hasScript(EngineQueueType.MapzoneExit, id)
    }

    private fun ZoneKey.hasEnterScript(): Boolean {
        return queueCache.hasScript(EngineQueueType.Zone, packed)
    }

    private fun ZoneKey.hasExitScript(): Boolean {
        return queueCache.hasScript(EngineQueueType.ZoneExit, packed)
    }

    private fun Player.processAreaChange() {
        if (coords == previousCoords) {
            return
        }
        val normalizedCoords = normalizedCoords()

        pendingAreas.clear()
        areaIndex.putAreas(normalizedCoords, pendingAreas)

        for (area in activeAreas.iterator()) {
            if (area in pendingAreas) {
                continue
            }
            engineQueueAreaExit(area)
        }

        for (area in pendingAreas.iterator()) {
            if (area in activeAreas) {
                continue
            }
            engineQueueArea(area)
        }

        activeAreas.clear()
        activeAreas.addAll(pendingAreas)
    }

    private fun Player.normalizedCoords(): CoordGrid =
        if (RegionRegistry.inWorkingArea(coords)) {
            regionRegistry.normalizeCoords(coords)
        } else {
            coords
        }
}
