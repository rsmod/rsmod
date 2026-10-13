package org.runerealm.api.player.hit.processor

import kotlin.math.min
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.headbars
import org.runerealm.api.config.refs.params
import org.runerealm.api.config.refs.queues
import org.runerealm.api.config.refs.spotanims
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.synths
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.player.headbar.InternalPlayerHeadbars
import org.runerealm.api.player.lefthand
import org.runerealm.api.player.output.soundSynth
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.baseHitpointsLvl
import org.runerealm.api.player.stat.hitpoints
import org.runerealm.api.player.stat.stat
import org.runerealm.api.player.stat.statBase
import org.runerealm.api.player.torso
import org.runerealm.api.random.GameRandom
import org.runerealm.game.headbar.Headbar
import org.runerealm.game.hit.Hit
import org.runerealm.game.hit.HitType
import org.runerealm.game.type.obj.UnpackedObjType
import org.runerealm.game.type.synth.SynthType

public object StandardPlayerHitProcessor : QueuedPlayerHitProcessor {
    private val hitSoundsBodyA =
        listOf(synths.human_hit_1, synths.human_hit_2, synths.human_hit_3, synths.human_hit_4)

    private val hitSoundsBodyB = listOf(synths.female_hit_1, synths.female_hit_2)

    override fun ProtectedAccess.process(hit: Hit) {
        if (!hit.isValid(this)) {
            return
        }
        preventLogout("You can't log out until 10 seconds after the end of combat.", 16)

        // TODO(combat): Process degradation, ring of recoil, etc. Retribution is handled on death.

        val damage = min(player.hitpoints, hit.damage)
        if (damage > 0) {
            statSub(stats.hitpoints, constant = damage, percent = 0)
        }

        applySmite(hit, damage)
        activateRedemption()
        playDefendSound(hit, random)

        val queueDeath = player.hitpoints == 0 && queues.death !in player.queueList
        if (queueDeath) {
            queueDeath()
        }

        player.showHitmark(hit.hitmark)

        val headbar = hit.createHeadbar(player.hitpoints, player.baseHitpointsLvl)
        player.showHeadbar(headbar)
    }

    /**
     * [Smite](https://oldschool.runescape.wiki/w/Smite): when the attacking player has Smite
     * active, 25% of the damage done (rounded down) is drained from the target's Prayer points.
     */
    private fun ProtectedAccess.applySmite(hit: Hit, damage: Int) {
        if (!hit.isFromPlayer || damage < SMITE_DIVISOR) {
            return
        }
        val source = findHitPlayerSource(hit) ?: return
        if (source.vars[varbits.smite] == 0) {
            return
        }
        val drain = min(player.stat(stats.prayer), damage / SMITE_DIVISOR)
        if (drain > 0) {
            statSub(stats.prayer, constant = drain, percent = 0)
        }
    }

    private const val SMITE_DIVISOR = 4

    /**
     * [Redemption](https://oldschool.runescape.wiki/w/Redemption): when hit to (or while below) 10%
     * of base Hitpoints, all Prayer points are drained and the player heals a quarter of their base
     * Prayer level.
     */
    private fun ProtectedAccess.activateRedemption() {
        if (player.vars[varbits.redemption] == 0) {
            return
        }
        val hitpoints = player.hitpoints
        if (hitpoints <= 0 || hitpoints * 10 >= player.baseHitpointsLvl) {
            return
        }
        val prayerBase = player.statBase(stats.prayer)
        statSub(stats.prayer, constant = player.stat(stats.prayer), percent = 0)
        statHeal(stats.hitpoints, constant = prayerBase / 4, percent = 0)
        spotanim(spotanims.redemption)
    }

    private fun Hit.isValid(access: ProtectedAccess): Boolean {
        // Currently, we only have evidence of this validation being applied to hits dealt by npcs.
        if (!isFromNpc) {
            return true
        }
        // Only melee-based hits can be invalidated here.
        if (type != HitType.Melee) {
            return true
        }
        // If the npc that dealt the hit can no longer be found, the hit is invalidated. This can
        // occur when the npc's internal `uid` is reassigned (e.g., due to transmogrification).
        val npc = access.findHitNpcSource(this) ?: return false
        return npc.hitpoints > 0
    }

    private fun ProtectedAccess.playDefendSound(hit: Hit, random: GameRandom) {
        val lefthandType = player.lefthand?.let(::ocType)
        val torsoType = player.torso?.let(::ocType)
        val bodyType = player.appearance.bodyType

        val defendSound = resolveDefendSound(lefthandType, torsoType, hit.damage, bodyType, random)
        soundSynth(defendSound, delay = 20)

        val playerSource = if (hit.isFromPlayer) findHitPlayerSource(hit) else null
        playerSource?.soundSynth(defendSound)
    }

    private fun resolveDefendSound(
        lefthand: UnpackedObjType?,
        torso: UnpackedObjType?,
        damage: Int,
        bodyType: Int,
        random: GameRandom,
    ): SynthType =
        when {
            damage == 0 -> resolveBlockSound(lefthand, torso, random)
            bodyType == constants.bodytype_a -> random.pick(hitSoundsBodyA)
            bodyType == constants.bodytype_b -> random.pick(hitSoundsBodyB)
            else -> throw NotImplementedError("Sound for body type is not implemented: $bodyType")
        }

    private fun resolveBlockSound(
        lefthand: UnpackedObjType?,
        torso: UnpackedObjType?,
        random: GameRandom,
    ): SynthType {
        val lefthandSound = lefthand?.randomBlockSound(random)
        if (lefthandSound != null) {
            return lefthandSound
        }

        val torsoSound = torso?.randomBlockSound(random)
        if (torsoSound != null) {
            return torsoSound
        }

        return synths.human_block_1
    }

    private fun UnpackedObjType.randomBlockSound(random: GameRandom): SynthType? {
        val sounds =
            listOfNotNull(
                paramOrNull(params.item_block_sound1),
                paramOrNull(params.item_block_sound2),
                paramOrNull(params.item_block_sound3),
                paramOrNull(params.item_block_sound4),
                paramOrNull(params.item_block_sound5),
            )
        return random.pickOrNull(sounds)
    }

    private fun Hit.createHeadbar(currHp: Int, maxHp: Int): Headbar =
        InternalPlayerHeadbars.createFromHitmark(hitmark, currHp, maxHp, headbars.health_30)
}
