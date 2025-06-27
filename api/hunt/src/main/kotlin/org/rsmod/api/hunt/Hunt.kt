package org.rsmod.api.hunt

import jakarta.inject.Inject
import org.rsmod.api.registry.loc.LocRegistry
import org.rsmod.api.registry.npc.NpcRegistry
import org.rsmod.api.registry.obj.ObjRegistry
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.api.route.RayCastValidator
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.loc.LocInfo
import org.rsmod.game.obj.Obj
import org.rsmod.game.type.hunt.HuntVis
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneGrid
import org.rsmod.map.zone.ZoneKey

public class Hunt
@Inject
constructor(
    private val validator: RayCastValidator,
    private val playerRegistry: PlayerRegistry,
    private val npcRegistry: NpcRegistry,
    private val objRegistry: ObjRegistry,
    private val locRegistry: LocRegistry,
) {
    public fun findPlayers(center: CoordGrid, maxDistance: Int, vis: HuntVis): Sequence<Player> {
        return sequence {
            val centerZone = ZoneKey.from(center)
            val zoneDistance = (maxDistance + ZONE_LENGTH_EXCLUSIVE) / ZoneGrid.LENGTH
            for (x in zoneDistance downTo -zoneDistance) {
                for (z in zoneDistance downTo -zoneDistance) {
                    val zone = centerZone.translate(x, z)
                    val players = playerRegistry.findAll(zone)
                    for (player in players) {
                        if (player.isInvisible) {
                            continue
                        }
                        val inRange = inHuntRange(center, player.coords, maxDistance, vis)
                        if (inRange) {
                            yield(player)
                        }
                    }
                }
            }
        }
    }

    public fun findLocs(center: CoordGrid, maxDistance: Int, vis: HuntVis): Sequence<LocInfo> {
        return sequence {
            val centerZone = ZoneKey.from(center)
            val zoneDistance = (maxDistance + ZONE_LENGTH_EXCLUSIVE) / ZoneGrid.LENGTH
            for (x in zoneDistance downTo -zoneDistance) {
                for (z in zoneDistance downTo -zoneDistance) {
                    val zone = centerZone.translate(x, z)
                    val locs = locRegistry.findAll(zone)
                    for (loc in locs) {
                        val inRange = inHuntRange(center, loc.coords, maxDistance, vis)
                        if (inRange) {
                            yield(loc)
                        }
                    }
                }
            }
        }
    }

    public fun findNpcs(center: CoordGrid, maxDistance: Int, vis: HuntVis): Sequence<Npc> {
        return sequence {
            val centerZone = ZoneKey.from(center)
            val zoneDistance = (maxDistance + ZONE_LENGTH_EXCLUSIVE) / ZoneGrid.LENGTH
            for (x in zoneDistance downTo -zoneDistance) {
                for (z in zoneDistance downTo -zoneDistance) {
                    val zone = centerZone.translate(x, z)
                    val npcs = npcRegistry.findAll(zone)
                    for (npc in npcs) {
                        if (npc.isInvisible) {
                            continue
                        }
                        val inRange = inHuntRange(center, npc.coords, maxDistance, vis)
                        if (inRange) {
                            yield(npc)
                        }
                    }
                }
            }
        }
    }

    public fun findObjs(center: CoordGrid, maxDistance: Int, vis: HuntVis): Sequence<Obj> {
        return sequence {
            val centerZone = ZoneKey.from(center)
            val zoneDistance = (maxDistance + ZONE_LENGTH_EXCLUSIVE) / ZoneGrid.LENGTH
            for (x in zoneDistance downTo -zoneDistance) {
                for (z in zoneDistance downTo -zoneDistance) {
                    val zone = centerZone.translate(x, z)
                    val objs = objRegistry.findAll(zone)
                    for (obj in objs) {
                        val inRange = inHuntRange(center, obj.coords, maxDistance, vis)
                        if (inRange) {
                            yield(obj)
                        }
                    }
                }
            }
        }
    }

    private fun inHuntRange(
        source: CoordGrid,
        target: CoordGrid,
        maxDistance: Int,
        vis: HuntVis,
    ): Boolean {
        val distance = source.chebyshevDistance(target)
        if (distance > maxDistance) {
            return false
        }
        return when (vis) {
            HuntVis.Off -> true
            HuntVis.LineOfSight -> validator.hasLineOfSight(source, target)
            HuntVis.LineOfWalk -> validator.hasLineOfWalk(source, target)
        }
    }

    private companion object {
        private const val ZONE_LENGTH_EXCLUSIVE = ZoneGrid.LENGTH - 1
    }
}
