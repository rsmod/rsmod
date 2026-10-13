package org.runerealm.api.game.process.player

import jakarta.inject.Inject
import org.runerealm.api.registry.player.PlayerRegistry
import org.runerealm.game.entity.Player
import org.runerealm.game.queue.EngineQueueCache
import org.runerealm.game.queue.EngineQueueType
import org.runerealm.map.square.MapSquareKey
import org.runerealm.map.zone.ZoneKey

public class PlayerMapUpdateProcessor
@Inject
constructor(private val queueCache: EngineQueueCache, private val playerRegistry: PlayerRegistry) {
    public fun process(player: Player) {
        player.processMapSquareChange()
        player.processZoneChange()
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
}
