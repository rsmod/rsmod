package org.rsmod.api.hunt

import jakarta.inject.Inject
import kotlin.math.abs
import org.rsmod.game.entity.Npc
import org.rsmod.game.interact.InteractionOp
import org.rsmod.game.type.category.CategoryType
import org.rsmod.game.type.hunt.HuntVis
import org.rsmod.game.type.npc.NpcType
import org.rsmod.game.type.npc.UnpackedNpcType
import org.rsmod.map.CoordGrid
import org.rsmod.map.zone.ZoneKey

public class NpcSearch @Inject constructor(private val hunt: Hunt) {
    /**
     * Searches for npcs around the [center] coordinate and returns the closest one that is within
     * [distance] tiles and has an `Op2`. If multiple npcs are equidistant, the last one found is
     * returned.
     */
    public fun hunt(center: CoordGrid, distance: Int, vis: HuntVis): Npc? {
        var minDistanceNpc: Npc? = null
        var minDistance = Int.MAX_VALUE

        val npcs = hunt.findNpcs(center, distance, vis)
        for (npc in npcs) {
            if (!npc.type.hasOp(InteractionOp.Op2)) {
                continue
            }
            val distance = center.euclideanSquaredDistance(npc.coords)
            if (distance <= minDistance) {
                minDistanceNpc = npc
                minDistance = distance
            }
        }

        return minDistanceNpc
    }

    /**
     * Searches for and returns all npcs around the [center] coordinate that are within [distance]
     * tiles, have an `Op2`, and their [Npc.type] matches the given [type].
     */
    public fun huntAll(
        center: CoordGrid,
        type: NpcType,
        distance: Int,
        vis: HuntVis,
    ): Sequence<Npc> {
        return sequence {
            val npcs = hunt.findNpcs(center, distance, vis)
            for (npc in npcs) {
                if (npc.id != type.id) {
                    continue
                }
                if (!npc.type.hasOp(InteractionOp.Op2)) {
                    continue
                }
                yield(npc)
            }
        }
    }

    /**
     * Searches for npcs around the [center] coordinate and returns the closest one that is within
     * [distance] tiles and their [Npc.type] matches the given [type]. If multiple npcs are
     * equidistant, the last one found is returned.
     *
     * _Unlike [hunt], this function can return npcs that do not have an `Op2`._
     */
    public fun find(center: CoordGrid, type: NpcType, distance: Int, vis: HuntVis): Npc? {
        var minDistanceNpc: Npc? = null
        var minDistance = Int.MAX_VALUE

        val npcs = hunt.findNpcs(center, distance, vis)
        for (npc in npcs) {
            if (npc.id != type.id) {
                continue
            }
            val distance = center.euclideanSquaredDistance(npc.coords)
            if (distance <= minDistance) {
                minDistanceNpc = npc
                minDistance = distance
            }
        }

        return minDistanceNpc
    }

    /**
     * Searches for npcs around the [center] coordinate and returns the closest one that is within
     * [distance] tiles and their [UnpackedNpcType.category] matches [category]. If multiple npcs
     * are equidistant, the last one found is returned.
     */
    public fun findCat(center: CoordGrid, type: CategoryType, distance: Int, vis: HuntVis): Npc? {
        var minDistanceNpc: Npc? = null
        var minDistance = Int.MAX_VALUE

        val npcs = hunt.findNpcs(center, distance, vis)
        for (npc in npcs) {
            if (npc.type.category != type.id) {
                continue
            }
            val distance = center.euclideanSquaredDistance(npc.coords)
            if (distance <= minDistance) {
                minDistanceNpc = npc
                minDistance = distance
            }
        }

        return minDistanceNpc
    }

    /**
     * Searches for and returns all npcs around the [center] coordinate that are within [distance]
     * tiles and their [Npc.type] matches the given [type].
     *
     * _Unlike [huntAll], this function can return npcs that do not have an `Op2`._
     */
    public fun findAll(
        center: CoordGrid,
        type: NpcType,
        distance: Int,
        vis: HuntVis,
    ): Sequence<Npc> {
        return hunt.findNpcs(center, distance, vis).filter { it.id == type.id }
    }

    /**
     * Searches for and returns all npcs around the [center] coordinate that are within [distance]
     * tiles and the same `center` [ZoneKey].
     *
     * _Unlike [huntAll], this function can return npcs that do not have an `Op2`._
     */
    public fun findAllZone(center: CoordGrid, distance: Int, vis: HuntVis): Sequence<Npc> {
        val zone = ZoneKey.from(center)
        return hunt.findNpcs(center, distance, vis).filter { ZoneKey.from(it.coords) == zone }
    }

    /**
     * Searches for and returns all npcs around the [center] coordinate that are within [distance]
     * tiles.
     *
     * _Unlike [huntAll], this function can return npcs that do not have an `Op2`._
     */
    public fun findAllAny(center: CoordGrid, distance: Int, vis: HuntVis): Sequence<Npc> {
        return hunt.findNpcs(center, distance, vis)
    }

    private fun CoordGrid.euclideanSquaredDistance(other: CoordGrid): Int {
        val dx = abs(x - other.x)
        val dz = abs(z - other.z)
        return dx * dx + dz * dz
    }
}
