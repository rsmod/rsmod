package org.rsmod.game.obj

import org.rsmod.game.MapClock
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjType
import org.rsmod.map.CoordGrid

/**
 * Represents an obj that exists on the game map at specific coordinates.
 *
 * Objs have different visibility scopes and ownership properties that determine which players can
 * see and interact with them.
 *
 * **Creation**: The primary constructor is reserved for **internal engine usage**. Instead, use the
 * appropriate companion object helper functions depending on your scenario:
 * - [fromPvp] - For objs dropped from player vs. player combat.
 * - [fromPvn] - For objs dropped from player vs. npc combat.
 * - [fromOwner] - For objs dropped by or for a specific player.
 * - [fromServer] - For server-spawned objs that should be instantly visible to everyone.
 *
 * **Visibility and Ownership**:
 * - [receiverId] determines who can currently see the obj.
 * - [ownerId] tracks the original owner (important for ironman restrictions).
 * - Objs can be private (visible to specific players) or public (visible to all)
 *
 * _Note: The aforementioned ids make use of [Player.observerUUID], which takes group game modes
 * into account._
 */
public class Obj(
    public val coords: CoordGrid,
    public var entity: ObjEntity,
    public val creationCycle: Int,
    public val receiverId: Long,
    public val ownerId: Long = NULL_OBSERVER_ID,
) {
    public val x: Int
        get() = coords.x

    public val z: Int
        get() = coords.z

    public val level: Int
        get() = coords.level

    public val type: Int
        get() = entity.id

    public val count: Int
        get() = entity.count

    public val scope: ObjScope
        get() = ObjScope[entity.scope]

    public val isPrivate: Boolean
        get() = entity.scope == ObjScope.Private.id

    public val isPublic: Boolean
        get() = !isPrivate

    /**
     * Provides a nullable representation of the [receiverId] to distinguish between an owned obj
     * and a non-owned obj.
     *
     * The `receiverId` field is non-nullable for performance reasons, and it defaults to
     * [NULL_OBSERVER_ID] to indicate an obj not owned by any specific player. In most cases, this
     * default value will not match a real observer's UUID. However, in the unlikely event that an
     * observer's UUID does match [NULL_OBSERVER_ID], this property helps identify the obj as
     * non-owned by returning `null`.
     *
     * This property can be used for safer checks when determining the ownership of an obj, avoiding
     * potential confusion with the reserved value: [NULL_OBSERVER_ID].
     *
     * @return `null` if the [receiverId] is equal to [NULL_OBSERVER_ID], otherwise the actual
     *   [receiverId].
     */
    public val nullableReceiverId: Long?
        get() = if (receiverId == NULL_OBSERVER_ID) null else receiverId

    /**
     * Provides a nullable representation of the [ownerId] to distinguish between the original owner
     * of the obj as opposed to the [receiverId].
     *
     * The `ownerId` field is non-nullable for performance reasons, and it defaults to
     * [NULL_OBSERVER_ID] to indicate an obj that did not originally belong to any specific player.
     * In most cases, this default value will not match a real observer's UUID. However, in the
     * unlikely event that an observer's UUID does match [NULL_OBSERVER_ID], this property helps
     * identify the obj as having no original owner by returning `null`.
     *
     * This property is particularly important for ironman game mode restrictions, where players can
     * only pick up objs that originally belonged to them. It can be used for safer ownership
     * checks, avoiding potential confusion with the reserved value: [NULL_OBSERVER_ID].
     *
     * @return `null` if the [ownerId] is equal to [NULL_OBSERVER_ID], otherwise the actual
     *   [ownerId] representing the original owner.
     */
    public val nullableOwnerId: Long?
        get() = if (ownerId == NULL_OBSERVER_ID) null else ownerId

    public fun reveal() {
        val entity = entity.copy(scope = ObjScope.Temp.id)
        this.entity = entity
    }

    public fun change(count: Int) {
        require(count > 0) { "`count` must be greater than 0. (count=$count)" }
        val entity = entity.copy(count = count)
        this.entity = entity
    }

    public fun isVisibleTo(player: Player): Boolean = isVisibleTo(player.observerUUID)

    public fun isVisibleTo(observer: Long?): Boolean = isPublic || observer == nullableReceiverId

    public fun isOriginalOwner(player: Player): Boolean = isOriginalOwner(player.observerUUID)

    public fun isOriginalOwner(observer: Long?): Boolean = observer == nullableOwnerId

    override fun toString(): String =
        "Obj(" +
            "coords=$coords, " +
            "type=$type, " +
            "count=$count, " +
            "scope=$scope, " +
            "creationCycle=$creationCycle, " +
            "receiverId=$nullableReceiverId" +
            ")"

    public companion object {
        public const val NULL_OBSERVER_ID: Long = Long.MIN_VALUE

        // TODO: Need to investigate certain interactions when it comes to ironman in pvp.
        //  - Can they pick up any of their death pile loot if the killer does not take it and
        //  it spawns publicly?

        public fun fromPvp(killer: Player, target: Player, obj: ObjType, count: Int): Obj {
            return fromPvp(killer, target, obj.id, count)
        }

        public fun fromPvp(killer: Player, target: Player, obj: InvObj): Obj {
            return fromPvp(killer, target, obj.id, obj.count)
        }

        private fun fromPvp(killer: Player, target: Player, obj: Int, count: Int): Obj {
            val entity = ObjEntity(obj, count, ObjScope.Private.id)
            return Obj(
                coords = target.coords,
                entity = entity,
                creationCycle = killer.currentMapClock,
                receiverId = killer.observerId(),
                ownerId = target.observerId(),
            )
        }

        public fun fromPvn(killer: Player, target: Npc, obj: ObjType, count: Int): Obj {
            return fromPvn(killer, target.coords, obj.id, count)
        }

        public fun fromPvn(killer: Player, target: Npc, obj: InvObj): Obj {
            return fromPvn(killer, target.coords, obj.id, obj.count)
        }

        private fun fromPvn(killer: Player, coords: CoordGrid, obj: Int, count: Int): Obj {
            val entity = ObjEntity(obj, count, ObjScope.Private.id)
            return Obj(
                coords = coords,
                entity = entity,
                creationCycle = killer.currentMapClock,
                receiverId = killer.observerId(),
                ownerId = killer.observerId(),
            )
        }

        public fun fromOwner(player: Player, coords: CoordGrid, obj: ObjType, count: Int): Obj {
            return fromOwner(player, coords, obj.id, count)
        }

        public fun fromOwner(player: Player, coords: CoordGrid, obj: InvObj): Obj {
            return fromOwner(player, coords, obj.id, obj.count)
        }

        private fun fromOwner(player: Player, coords: CoordGrid, obj: Int, count: Int): Obj {
            val entity = ObjEntity(obj, count, ObjScope.Private.id)
            return Obj(
                coords = coords,
                entity = entity,
                creationCycle = player.currentMapClock,
                receiverId = player.observerId(),
                ownerId = player.observerId(),
            )
        }

        public fun fromServer(clock: MapClock, coords: CoordGrid, obj: ObjType, count: Int): Obj {
            return fromServer(clock, coords, obj.id, count)
        }

        public fun fromServer(clock: MapClock, coords: CoordGrid, obj: InvObj): Obj {
            return fromServer(clock, coords, obj.id, obj.count)
        }

        private fun fromServer(clock: MapClock, coords: CoordGrid, obj: Int, count: Int): Obj {
            val entity = ObjEntity(obj, count, ObjScope.Temp.id)
            return Obj(coords, entity, clock.cycle, NULL_OBSERVER_ID)
        }

        private fun Player.observerId(): Long {
            return observerUUID ?: error("`observerUUID` not set for player: $this")
        }
    }
}
