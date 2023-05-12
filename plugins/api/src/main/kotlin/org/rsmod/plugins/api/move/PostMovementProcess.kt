package org.rsmod.plugins.api.move

import org.openrs2.crypto.XteaKey
import org.rsmod.game.map.square.MapSquareKey
import org.rsmod.game.map.zone.ZoneKey
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.game.model.mob.list.forEachNotNull
import org.rsmod.plugins.api.cache.map.xtea.XteaRepository
import org.rsmod.plugins.api.model.event.MapEvent
import org.rsmod.plugins.api.net.downstream.RebuildNormal
import org.rsmod.plugins.api.publish
import org.rsmod.plugins.api.refreshBuildArea
import org.rsmod.plugins.api.util.BuildAreaUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class PostMovementProcess @Inject constructor(
    private val players: PlayerList,
    private val xteaRepository: XteaRepository
) {

    public fun execute() {
        players.forEachNotNull {
            it.checkViewportBoundary()
            it.checkZoneChange()
            it.checkMapSquareChange()
        }
    }

    private fun Player.checkViewportBoundary() {
        if (!shouldRebuildViewport()) return
        writeRebuildNormal()
        refreshBuildArea(coords)
    }

    private fun Player.checkZoneChange() {
        val oldZone = ZoneKey.from(prevCoords)
        val newZone = ZoneKey.from(coords)
        if (newZone == oldZone) return
        publish(newZone.packed, MapEvent.ZoneChange)
    }

    private fun Player.checkMapSquareChange() {
        val oldMapSquare = MapSquareKey.from(prevCoords)
        val newMapSquare = MapSquareKey.from(coords)
        if (oldMapSquare == newMapSquare) return
        publish(newMapSquare.id, MapEvent.MapSquareChange)
    }

    private fun Player.writeRebuildNormal() {
        val zoneKey = ZoneKey.from(coords)
        val xtea = buildList {
            val mapSquares = zoneKey.toViewport(BuildAreaUtils.ZONE_VIEW_RADIUS)
            mapSquares.forEach { mapSquare ->
                val key = xteaRepository[mapSquare] ?: XteaKey.ZERO
                this += key.k0
                this += key.k1
                this += key.k2
                this += key.k3
            }
        }
        val rebuildNormal = RebuildNormal(null, zoneKey, xtea)
        downstream += rebuildNormal
    }

    private fun Player.shouldRebuildViewport(): Boolean {
        val dx = coords.x - buildArea.base.x
        val dz = coords.z - buildArea.base.z
        return dx < BuildAreaUtils.REBUILD_BOUNDARY || dz < BuildAreaUtils.REBUILD_BOUNDARY ||
            dx >= BuildAreaUtils.SIZE - BuildAreaUtils.REBUILD_BOUNDARY ||
            dz >= BuildAreaUtils.SIZE - BuildAreaUtils.REBUILD_BOUNDARY
    }
}
