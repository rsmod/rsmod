package org.rsmod.api.combat.commons.player

import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.interact.PlayerInteractions
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.player.vars.boolVarp
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.NpcList
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.entity.npc.NpcUid
import org.rsmod.game.entity.player.PlayerUid
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.seq.SeqType

private val ProtectedAccess.autoRetaliateDisabled by boolVarp(varps.auto_retaliate_disabled)

public fun Player.queueCombatRetaliate(source: Npc, delay: Int = 1) {
    strongQueue(queues.com_retaliate_npc, delay, source.uid)
}

public fun ProtectedAccess.combatRetaliate(
    uid: NpcUid,
    flinchDelay: Int,
    npcList: NpcList,
    interactions: NpcInteractions,
) {
    if (autoRetaliateDisabled || isBusy2) {
        return
    }
    val source = uid.resolve(npcList) ?: return

    if (actionDelay < mapClock) {
        actionDelay = mapClock + flinchDelay
    }

    opNpc2(source, interactions)
}

public fun Player.queueCombatRetaliate(source: Player, delay: Int = 1) {
    strongQueue(queues.com_retaliate_player, delay, source.uid)
}

public fun ProtectedAccess.combatRetaliate(
    uid: PlayerUid,
    flinchDelay: Int,
    playerList: PlayerList,
    interactions: PlayerInteractions,
) {
    preventLogout("You can't log out until 10 seconds after the end of combat.", 16)
    if (autoRetaliateDisabled || isBusy2) {
        return
    }
    val source = uid.resolve(playerList) ?: return

    if (actionDelay < mapClock) {
        actionDelay = mapClock + flinchDelay
    }

    opPlayer2(source, interactions)
}

public fun Player.combatPlayDefendAnim(objTypes: ObjTypeList, clientDelay: Int = 0) {
    val righthandType = righthand?.let(objTypes::get)
    val lefthandType = lefthand?.let(objTypes::get)
    val defendAnim = resolveDefendAnim(righthandType, lefthandType)
    anim(defendAnim, delay = clientDelay)
}

private fun resolveDefendAnim(righthand: UnpackedObjType?, lefthand: UnpackedObjType?): SeqType {
    val righthandAnim = righthand?.param(params.defend_anim)
    val lefthandAnim = lefthand?.param(params.defend_anim)
    return when {
        lefthandAnim != null && !lefthandAnim.isType(seqs.human_unarmedblock) -> lefthandAnim
        righthandAnim != null -> righthandAnim
        else -> seqs.human_unarmedblock
    }
}
