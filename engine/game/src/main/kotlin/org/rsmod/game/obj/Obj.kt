package org.rsmod.game.obj

import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjType
import org.rsmod.map.CoordGrid

public class Obj(
    public val coords: CoordGrid,
    public var entity: ObjEntity,
    public val creationCycle: Int,
    public val receiverId: Long,
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
     * [NULL_RECEIVER_ID] to indicate an obj that isn't owned by any specific player. In most cases,
     * this default value will not match a real observer's UUID. However, in the unlikely event that
     * an observer's UUID does match [NULL_RECEIVER_ID], this property helps identify the obj as
     * non-owned by returning `null`.
     *
     * This property can be used for safer checks when determining the ownership of an obj, avoiding
     * potential confusion with the reserved value: [NULL_RECEIVER_ID].
     *
     * @return `null` if the [receiverId] is equal to [NULL_RECEIVER_ID], otherwise the actual
     *   [receiverId].
     */
    public val nullableReceiverId: Long?
        get() = if (receiverId == NULL_RECEIVER_ID) null else receiverId

    public constructor(
        coords: CoordGrid,
        type: ObjType,
        count: Int,
        creationCycle: Int,
        receiver: Player,
    ) : this(coords, entity(type, count, ObjScope.Private), creationCycle, receiver.observerId())

    public constructor(
        coords: CoordGrid,
        type: ObjType,
        count: Int,
        creationCycle: Int,
        receiverId: Long,
    ) : this(coords, entity(type, count, ObjScope.Private), creationCycle, receiverId)

    public constructor(
        coords: CoordGrid,
        type: ObjType,
        count: Int,
        creationCycle: Int,
    ) : this(coords, entity(type, count, ObjScope.Temp), creationCycle, NULL_RECEIVER_ID)

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
        public const val NO_DESPAWN_CLOCK: Int = -1
        public const val NULL_RECEIVER_ID: Long = Long.MIN_VALUE

        private fun entity(type: ObjType, count: Int, scope: ObjScope): ObjEntity =
            ObjEntity(type.id, count, scope.id)

        private fun Player.observerId(): Long =
            observerUUID ?: error("`observerUUID` not set for player: $this")
    }
}
