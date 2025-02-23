package org.rsmod.game.entity.util

import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.map.collision.isZoneValid
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.movement.RouteRequestCoord
import org.rsmod.game.seq.EntitySeq
import org.rsmod.game.spot.EntitySpotanim
import org.rsmod.game.type.seq.SeqType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.map.CoordGrid
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
    public fun walk(entity: PathingEntity, dest: CoordGrid) {
        val request = RouteRequestCoord(dest)
        entity.routeRequest = request
    }

    public fun telejump(entity: PathingEntity, collision: CollisionFlagMap, dest: CoordGrid) {
        telemove(entity, collision, dest)
        entity.moveSpeed = MoveSpeed.Stationary
    }

    public fun teleport(entity: PathingEntity, collision: CollisionFlagMap, dest: CoordGrid) {
        telemove(entity, collision, dest)
        entity.lastMovement = entity.currentMapClock
    }

    public fun telemove(entity: PathingEntity, collision: CollisionFlagMap, dest: CoordGrid) {
        check(collision.isZoneValid(dest)) {
            "Entity cannot be moved to an invalid zone: entity=$entity, dest=$dest"
        }

        val start = entity.coords
        entity.coords = dest
        // Need to set move speed so that movement processor knows to consume
        // steps for this entity.
        entity.moveSpeed = MoveSpeed.Walk
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

    public fun facePlayer(entity: PathingEntity, target: Player) {
        entity.faceEntity = EntityFaceTarget(target)
    }

    public fun faceNpc(entity: PathingEntity, target: Npc) {
        entity.faceEntity = EntityFaceTarget(target)
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

    public fun spotanim(
        entity: PathingEntity,
        spot: SpotanimType,
        delay: Int,
        height: Int,
        slot: Int,
    ) {
        val spotanim = EntitySpotanim(spot.id, delay, height, slot)
        entity.pendingSpotanims.add(spotanim.packed)
    }
}
