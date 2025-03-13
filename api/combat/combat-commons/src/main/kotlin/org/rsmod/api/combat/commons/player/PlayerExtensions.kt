package org.rsmod.api.combat.commons.player

import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.queues
import org.rsmod.api.config.refs.seqs
import org.rsmod.api.config.refs.synths
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.interact.PlayerInteractions
import org.rsmod.api.player.lefthand
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.player.torso
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
import org.rsmod.game.type.synth.SynthType

private val ProtectedAccess.autoRetaliate by boolVarp(varps.auto_retaliate)

private val hitSoundsBodyA =
    listOf(synths.human_hit_1, synths.human_hit_2, synths.human_hit_3, synths.human_hit_4)

private val hitSoundsBodyB = listOf(synths.female_hit_1, synths.female_hit_2)

public fun Player.queueCombatRetaliate(source: Npc, delay: Int = 1) {
    strongQueue(queues.com_retaliate_npc, delay, source.uid)
}

public fun ProtectedAccess.combatRetaliate(
    uid: NpcUid,
    flinchDelay: Int,
    npcList: NpcList,
    interactions: NpcInteractions,
) {
    if (!autoRetaliate || isBusy2) {
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
    // TODO(combat): Set `p_preventlogout` for 16 ticks
    if (!autoRetaliate || isBusy2) {
        return
    }
    val source = uid.resolve(playerList) ?: return

    if (actionDelay < mapClock) {
        actionDelay = mapClock + flinchDelay
    }

    opPlayer2(source, interactions)
}

public fun Player.combatPlayDefendFx(damage: Int, objTypes: ObjTypeList) {
    val righthandType = righthand?.let(objTypes::get)
    val lefthandType = lefthand?.let(objTypes::get)

    val defendAnim = resolveDefendAnim(righthandType, lefthandType)
    anim(defendAnim, 0, defendAnim.priority)

    val torsoType = torso?.let(objTypes::get)
    val defendSound = resolveDefendSound(lefthandType, torsoType, damage, appearance.bodyType)
    soundSynth(defendSound, delay = 20)
}

public fun Player.combatPlayDefendFx(source: Player, damage: Int, objTypes: ObjTypeList) {
    val righthandType = righthand?.let(objTypes::get)
    val lefthandType = lefthand?.let(objTypes::get)

    val defendAnim = resolveDefendAnim(righthandType, lefthandType)
    anim(defendAnim, 0, defendAnim.priority)

    val torsoType = torso?.let(objTypes::get)
    val defendSound = resolveDefendSound(lefthandType, torsoType, damage, appearance.bodyType)
    soundSynth(defendSound, delay = 20)

    source.soundSynth(defendSound)
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

private fun resolveDefendSound(
    lefthand: UnpackedObjType?,
    torso: UnpackedObjType?,
    damage: Int,
    bodyType: Int,
): SynthType {
    return when {
        damage == 0 -> resolveBlockSound(lefthand, torso)
        bodyType == constants.bodytype_a -> hitSoundsBodyA.random()
        bodyType == constants.bodytype_b -> hitSoundsBodyB.random()
        else -> throw NotImplementedError("Sound for body type is not implemented: $bodyType")
    }
}

private fun resolveBlockSound(lefthand: UnpackedObjType?, torso: UnpackedObjType?): SynthType {
    val lefthandSound = lefthand?.randomBlockSound()
    if (lefthandSound != null) {
        return lefthandSound
    }

    val torsoSound = torso?.randomBlockSound()
    if (torsoSound != null) {
        return torsoSound
    }

    return synths.human_block_1
}

private fun UnpackedObjType.randomBlockSound(): SynthType? {
    val sounds =
        listOfNotNull(
            paramOrNull(params.item_block_sound1),
            paramOrNull(params.item_block_sound2),
            paramOrNull(params.item_block_sound3),
            paramOrNull(params.item_block_sound4),
            paramOrNull(params.item_block_sound5),
        )
    return sounds.randomOrNull()
}
