package org.runerealm.game.hit

import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.NpcList
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList
import org.runerealm.game.entity.npc.NpcUid
import org.runerealm.game.entity.player.PlayerUid
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.obj.UnpackedObjType

public data class Hit(
    public val type: HitType,
    public val hitmark: Hitmark,
    private val sourceUid: Int?,
    private val righthandObj: Int?,
    private val secondaryObj: Int?,
) {
    public val damage: Int
        get() = hitmark.damage

    public val isFromNpc: Boolean
        get() = hitmark.isNpcSource

    public val isFromPlayer: Boolean
        get() = hitmark.isPlayerSource

    public fun isRighthandObj(type: ObjType): Boolean = type.id == righthandObj

    public fun righthandType(objTypes: ObjTypeList): UnpackedObjType? = objTypes[righthandObj]

    public fun isSecondaryObj(type: ObjType): Boolean = type.id == secondaryObj

    public fun secondaryType(objTypes: ObjTypeList): UnpackedObjType? = objTypes[secondaryObj]

    public fun resolveNpcSource(npcList: NpcList): Npc? {
        val uid = checkNotNull(sourceUid) { "Hit did not originate from a source: $this" }
        check(isFromNpc) { "Hit did not originate from an npc: $this" }

        val npcUid = NpcUid(uid)
        return npcUid.resolve(npcList)
    }

    public fun resolvePlayerSource(playerList: PlayerList): Player? {
        val uid = checkNotNull(sourceUid) { "Hit did not originate from a source: $this" }
        check(isFromPlayer) { "Hit did not originate from a player: $this" }

        val playerUid = PlayerUid(uid)
        return playerUid.resolve(playerList)
    }

    override fun toString(): String =
        "Hit(type=$type, righthandObj=$righthandObj, secondaryObj=$secondaryObj, hitmark=$hitmark)"
}
