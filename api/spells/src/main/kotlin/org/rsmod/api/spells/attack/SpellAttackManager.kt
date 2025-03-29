package org.rsmod.api.spells.attack

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.magic.MagicSpell
import org.rsmod.api.combat.manager.MagicRuneManager
import org.rsmod.api.combat.manager.PlayerAttackManager
import org.rsmod.api.config.refs.spotanims
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.synths
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.Hit
import org.rsmod.game.proj.ProjAnim
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.proj.ProjAnimType
import org.rsmod.game.type.spot.SpotanimType
import org.rsmod.game.type.synth.SynthType

public class SpellAttackManager
@Inject
constructor(private val manager: PlayerAttackManager, private val runes: MagicRuneManager) {
    private val ProtectedAccess.autocastEnabled by boolVarBit(varbits.autocast_enabled)

    /**
     * Checks and consumes any requirements for [CombatAttack.Spell.spell], delegating to
     * [MagicRuneManager.attemptCast] to determine success.
     *
     * _Note: If the spell cannot be cast, this function will implicitly call [clearCombat],
     * resetting the player's attack delay since the cast should not proceed._
     *
     * @see [MagicRuneManager.attemptCast]
     * @see [clearCombat]
     */
    public fun attemptCast(source: ProtectedAccess, attack: CombatAttack.Spell): Boolean {
        val canCast = runes.attemptCast(source.player, attack.spell)
        if (!canCast) {
            clearCombat(source)
            return false
        }
        return true
    }

    /**
     * Grants Magic experience to the player based on [MagicSpell.castXp] from the given
     * [CombatAttack.Spell.spell].
     */
    public fun giveCastXp(access: ProtectedAccess, attack: CombatAttack.Spell) {
        manager.statAdvance(access.player, stats.magic, attack.spell.castXp, multiplier = 1.0)
    }

    /**
     * Delegates to [PlayerAttackManager.continueCombat] if the [source] player is autocasting.
     * Otherwise, it breaks off any further combat interactions with the [target].
     *
     * _Note: The continued combat interaction will use the player's autocast spell, not necessarily
     * the spell cast during this interaction._
     *
     * @see [PlayerAttackManager.continueCombat]
     */
    public fun continueCombatIfAutocast(source: ProtectedAccess, target: PathingEntity) {
        if (!source.autocastEnabled) {
            manager.stopCombat(source.player)
            return
        }
        manager.continueCombat(source.player, target)
    }

    /** @see [PlayerAttackManager.clearCombat] */
    public fun clearCombat(access: ProtectedAccess) {
        manager.clearCombat(access.player)
    }

    /** @see [PlayerAttackManager.giveCombatXp] */
    public fun giveCombatXp(
        source: ProtectedAccess,
        target: PathingEntity,
        attack: CombatAttack.Spell,
        damage: Int,
    ) {
        manager.giveCombatXp(source.player, target, attack, damage)
    }

    /**
     * Queues a **non-splash** hit on [target]. Unlike splash hits, this will queue auto-retaliation
     * with a delay of [hitDelay] cycles.
     *
     * _To queue a splash hit, use [queueSplashHit]._
     *
     * @see [PlayerAttackManager.queueMagicHit]
     */
    public fun queueMagicHit(
        source: ProtectedAccess,
        target: PathingEntity,
        spell: ObjType,
        damage: Int,
        clientDelay: Int,
        hitDelay: Int = 1 + (clientDelay / 30),
    ): Hit =
        manager.queueMagicHit(
            source = source.player,
            target = target,
            spell = spell,
            damage = damage,
            clientDelay = clientDelay,
            hitDelay = hitDelay,
            retaliationDelay = hitDelay,
        )

    /**
     * Queues a **splash** hit on [target]. Unlike non-splash hits, this will queue auto-retaliation
     * with an "immediate" delay (i.e., after 1 server cycle).
     *
     * _To queue a non-splash hit, use [queueMagicHit]._
     *
     * @see [PlayerAttackManager.queueMagicHit]
     */
    public fun queueSplashHit(
        source: ProtectedAccess,
        target: PathingEntity,
        spell: ObjType,
        clientDelay: Int,
        hitDelay: Int = 1 + (clientDelay / 30),
    ): Hit =
        manager.queueMagicHit(
            source = source.player,
            target = target,
            spell = spell,
            damage = 0,
            clientDelay = clientDelay,
            hitDelay = hitDelay,
            retaliationDelay = 1,
        )

    /**
     * Plays the cast sound, hit spotanim, and hit sound effects for a successful (non-splash) hit
     * on [target].
     *
     * This includes visual and sound effects timed according to [clientDelay], using [castSound],
     * [hitSpot], and [hitSound] if they are not `null`.
     */
    public fun playHitFx(
        source: ProtectedAccess,
        target: PathingEntity,
        clientDelay: Int,
        castSound: SynthType?,
        soundRadius: Int,
        hitSpot: SpotanimType?,
        hitSpotHeight: Int,
        hitSound: SynthType?,
    ): Unit =
        when (target) {
            is Npc ->
                playHitFx(
                    source = source,
                    target = target,
                    clientDelay = clientDelay,
                    castSound = castSound,
                    hitSpot = hitSpot,
                    hitSpotHeight = hitSpotHeight,
                    hitSound = hitSound,
                )
            is Player ->
                playHitFx(
                    source = source,
                    target = target,
                    clientDelay = clientDelay,
                    castSound = castSound,
                    soundRadius = soundRadius,
                    hitSpot = hitSpot,
                    hitSpotHeight = hitSpotHeight,
                    hitSound = hitSound,
                )
        }

    private fun playHitFx(
        source: ProtectedAccess,
        target: Npc,
        clientDelay: Int,
        castSound: SynthType?,
        hitSpot: SpotanimType?,
        hitSpotHeight: Int,
        hitSound: SynthType?,
    ) {
        if (castSound != null) {
            source.soundSynth(castSound)
        }

        if (hitSpot != null) {
            target.spotanim(hitSpot, delay = clientDelay, height = hitSpotHeight)
        }

        if (hitSound != null) {
            soundArea(target, hitSound, delay = clientDelay, radius = 10)
        }
    }

    private fun playHitFx(
        source: ProtectedAccess,
        target: Player,
        clientDelay: Int,
        castSound: SynthType?,
        soundRadius: Int,
        hitSpot: SpotanimType?,
        hitSpotHeight: Int,
        hitSound: SynthType?,
    ) {
        if (castSound != null) {
            soundArea(source.player, castSound, radius = soundRadius)
        }

        if (hitSpot != null) {
            target.spotanim(hitSpot, delay = clientDelay, height = hitSpotHeight)
        }

        if (hitSound != null) {
            soundArea(target, hitSound, delay = clientDelay, radius = 10)
        }
    }

    /**
     * Plays the cast sound, splash spotanim, and splash sound effects for a missed hit (splash) on
     * [target].
     *
     * This includes visual and sound effects timed according to [clientDelay], using [castSound] if
     * not `null`.
     */
    public fun playSplashFx(
        source: ProtectedAccess,
        target: PathingEntity,
        clientDelay: Int,
        castSound: SynthType?,
        soundRadius: Int,
    ): Unit =
        when (target) {
            is Npc ->
                playSplashFx(
                    source = source,
                    target = target,
                    clientDelay = clientDelay,
                    castSound = castSound,
                )
            is Player ->
                playSplashFx(
                    source = source,
                    target = target,
                    clientDelay = clientDelay,
                    castSound = castSound,
                    soundRadius = soundRadius,
                )
        }

    private fun playSplashFx(
        source: ProtectedAccess,
        target: Npc,
        clientDelay: Int,
        castSound: SynthType?,
    ) {
        if (castSound != null) {
            source.soundSynth(castSound)
        }
        target.spotanim(spotanims.splash, delay = clientDelay, height = 124)
        soundArea(target, synths.spellfail, delay = clientDelay, radius = 10)
    }

    private fun playSplashFx(
        source: ProtectedAccess,
        target: Player,
        clientDelay: Int,
        castSound: SynthType?,
        soundRadius: Int,
    ) {
        if (castSound != null) {
            soundArea(source.player, castSound, radius = soundRadius)
        }
        target.spotanim(spotanims.splash, delay = clientDelay, height = 124)
        soundArea(target, synths.spellfail, delay = clientDelay, radius = 10)
    }

    /** @see [PlayerAttackManager.spawnProjectile] */
    public fun spawnProjectile(
        source: ProtectedAccess,
        target: PathingEntity,
        spotanim: SpotanimType,
        projanim: ProjAnimType,
    ): ProjAnim = manager.spawnProjectile(source.player, target, spotanim, projanim)

    /** @see [PlayerAttackManager.soundArea] */
    public fun soundArea(
        source: PathingEntity,
        synth: SynthType,
        delay: Int = 0,
        loops: Int = 1,
        radius: Int = 5,
        size: Int = 0,
    ): Unit =
        manager.soundArea(
            source.coords,
            synth = synth,
            delay = delay,
            loops = loops,
            radius = radius,
            size = size,
        )
}
