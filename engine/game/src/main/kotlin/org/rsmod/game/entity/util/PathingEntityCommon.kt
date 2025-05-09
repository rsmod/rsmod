package org.rsmod.game.entity.util

import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.map.collision.isZoneValid
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.seq.EntitySeq
import org.rsmod.game.spot.EntitySpotanim
import org.rsmod.game.type.seq.SeqType
import org.rsmod.map.CoordGrid
import org.rsmod.map.util.Translation
import org.rsmod.routefinder.collision.CollisionFlagMap

/**
 * This class serves as a shared namespace for functions and logic common to all
 * [org.rsmod.game.entity.PathingEntity] implementations. It allows for the reuse of code that
 * multiple entities need to share, while also accommodating the need for certain entities to handle
 * additional, specialized logic after invoking these common functions.
 *
 * For example, both [org.rsmod.game.entity.Npc] and [org.rsmod.game.entity.Player] can use the same
 * logic for facing another player. However, NPCs might require extra steps, such as updating
 * related state with extended info, whereas Players may only need to rely on the base
 * functionality.
 *
 * Additionally, concepts like "protected access" influence the exposure of these functions. For
 * instance, NPCs do not have the concept of protected access and can call methods like
 * [org.rsmod.game.entity.Npc.telejump] directly. Players, however, may require these functions to
 * be more restricted to ensure proper access control.
 *
 * By centralizing this logic here, we minimize code duplication and ensure consistent behavior
 * across different entities, while still allowing for the flexibility needed by specific
 * implementations.
 */
public object PathingEntityCommon {
    // We don't declare or use [org.rsmod.game.entity.NpcList]'s capacity because some
    // developers will more than likely be tempted to fiddle with that npc list capacity
    // at some point. In that case, we do not want the npc list capacity, whether lower
    // or higher, to affect this value used for internal systems.
    internal const val INTERNAL_NPC_LIMIT = 65535

    public fun telejump(entity: PathingEntity, collision: CollisionFlagMap, dest: CoordGrid) {
        telemove(entity, collision, dest)
        entity.pendingTelejump = true
        entity.moveSpeed = MoveSpeed.Stationary
    }

    public fun teleport(entity: PathingEntity, collision: CollisionFlagMap, dest: CoordGrid) {
        telemove(entity, collision, dest)
        entity.pendingTeleport = true
        entity.moveSpeed = MoveSpeed.Walk
        entity.lastMovement = entity.currentMapClock
    }

    private fun telemove(entity: PathingEntity, collision: CollisionFlagMap, dest: CoordGrid) {
        check(collision.isZoneValid(dest)) {
            "Entity cannot be moved to an invalid zone: entity=$entity, dest=$dest"
        }

        val start = entity.coords
        entity.coords = dest
        // Reset any ongoing movement and/or interaction.
        entity.abortRoute()
        entity.clearInteraction()
        // Instantly move collision flag from previous coords to new coords.
        // This is important as it covers specific edge cases where processing order have differing
        // outcomes if the collision flags have not been moved for other entities in the world.
        collision.move(entity, start, dest)
    }

    private fun CollisionFlagMap.move(entity: PathingEntity, from: CoordGrid, to: CoordGrid) {
        entity.removeBlockWalkCollision(this, from)
        entity.addBlockWalkCollision(this, to)
    }

    public fun exactMove(
        entity: PathingEntity,
        start: CoordGrid,
        end: CoordGrid,
        delay1: Int,
        delay2: Int,
        dir: Int,
        collision: CollisionFlagMap,
    ) {
        telejump(entity, collision, end)
        val delta1 = Translation.between(start, end)
        val delta2 = Translation.between(end, entity.coords)
        val exactMove =
            EntityExactMove(
                deltaX1 = delta1.x,
                deltaZ1 = delta1.z,
                clientDelay1 = delay1,
                deltaX2 = delta2.x,
                deltaZ2 = delta2.z,
                clientDelay2 = delay2,
                direction = dir,
            )
        entity.pendingExactMove = exactMove
    }

    public fun facePlayer(entity: PathingEntity, target: Player) {
        entity.faceEntity = EntityFaceTarget(target)
        entity.lastFaceEntity = entity.currentMapClock
    }

    public fun faceNpc(entity: PathingEntity, target: Npc) {
        entity.faceEntity = EntityFaceTarget(target)
        entity.lastFaceEntity = entity.currentMapClock
    }

    public fun resetFaceEntity(entity: PathingEntity) {
        entity.faceEntity = EntityFaceTarget.NULL
    }

    public fun anim(entity: PathingEntity, seq: SeqType, delay: Int, priority: Int): Boolean {
        require(delay in 0..254) { "`delay` must be within range [0..254]." }
        require(priority in 0..254) { "`priority` must be within range [0..254]." }

        if (entity.animProtect) {
            return false
        }

        val hasPriority = entity.pendingSequence.isLowerPriorityThan(priority)
        if (!hasPriority) {
            return false
        }

        entity.pendingSequence = EntitySeq(seq.id, delay, priority)
        return true
    }

    private fun EntitySeq.isLowerPriorityThan(otherPriority: Int): Boolean =
        when (this) {
            EntitySeq.NULL -> true
            EntitySeq.ZERO -> true
            else -> otherPriority > priority
        }

    public fun setAnimProtect(entity: PathingEntity, animProtect: Boolean) {
        entity.animProtect = animProtect
    }

    public fun spotanim(entity: PathingEntity, spot: Int, delay: Int, height: Int, slot: Int) {
        val spotanim = EntitySpotanim(spot, delay, height, slot)
        entity.pendingSpotanims.add(spotanim.packed)
    }
}
