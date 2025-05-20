package org.rsmod.api.combat.commons.player

import kotlin.math.min
import org.rsmod.api.config.refs.categories
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.player.vars.boolVarp
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.npc.NpcUid
import org.rsmod.game.entity.player.PlayerUid
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.seq.SeqType

private val ProtectedAccess.autoRetaliateDisabled by boolVarp(varps.option_nodef)

public fun Player.queueCombatRetaliate(source: Npc, delay: Int = 1) {
    strongQueue(queues.com_retaliate_npc, delay, source.uid)
}

public fun ProtectedAccess.combatRetaliate(uid: NpcUid, flinchDelay: Int) {
    if (autoRetaliateDisabled || isBusy2) {
        return
    }
    val source = findUid(uid) ?: return

    if (actionDelay < mapClock) {
        actionDelay = mapClock + flinchDelay
    }

    opNpc2(source)
}

public fun Player.queueCombatRetaliate(source: Player, delay: Int = 1) {
    strongQueue(queues.com_retaliate_player, delay, source.uid)
}

public fun ProtectedAccess.combatRetaliate(uid: PlayerUid, flinchDelay: Int) {
    preventLogout("You can't log out until 10 seconds after the end of combat.", 16)
    if (autoRetaliateDisabled || isBusy2) {
        return
    }
    val source = findUid(uid) ?: return

    if (actionDelay < mapClock) {
        actionDelay = mapClock + flinchDelay
    }

    opPlayer2(source)
}

public fun Player.combatPlayDefendAnim(objTypes: ObjTypeList, clientDelay: Int = 0) {
    val righthandType = objTypes.getOrNull(righthand)
    val lefthandType = objTypes.getOrNull(lefthand)
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

public fun Player.combatPlayDefendSpot(objTypes: ObjTypeList, ammo: ObjType?, clientDelay: Int) {
    val type = ammo?.let(objTypes::get) ?: return
    if (!type.isCategoryType(categories.javelin)) {
        return
    }
    spotanim(spotanims.ballista_special, delay = clientDelay, height = 146)
}

public fun Player.resolveCombatXpMultiplier(): Double = min(1.125, 1 + (0.025 * (combatLevel / 20)))
