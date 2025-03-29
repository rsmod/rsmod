package org.rsmod.api.spells.attack

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.magic.MagicSpell
import org.rsmod.api.combat.manager.MagicRuneManager
import org.rsmod.api.combat.manager.PlayerAttackManager
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.game.entity.PathingEntity
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
        access.statAdvance(stats.magic, attack.spell.castXp)
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

    /** @see [PlayerAttackManager.queueSplashHit] */
    public fun queueSplashHit(
        source: ProtectedAccess,
        target: PathingEntity,
        spell: ObjType,
        clientDelay: Int,
        hitDelay: Int = 1 + (clientDelay / 30),
    ): Hit =
        manager.queueSplashHit(
            source = source.player,
            target = target,
            spell = spell,
            clientDelay = clientDelay,
            hitDelay = hitDelay,
        )

    /** @see [PlayerAttackManager.playMagicHitFx] */
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
        manager.playMagicHitFx(
            source = source.player,
            target = target,
            clientDelay = clientDelay,
            castSound = castSound,
            soundRadius = soundRadius,
            hitSpot = hitSpot,
            hitSpotHeight = hitSpotHeight,
            hitSound = hitSound,
        )

    /** @see [PlayerAttackManager.playMagicSplashFx] */
    public fun playSplashFx(
        source: ProtectedAccess,
        target: PathingEntity,
        clientDelay: Int,
        castSound: SynthType?,
        soundRadius: Int,
    ): Unit =
        manager.playMagicSplashFx(
            source = source.player,
            target = target,
            clientDelay = clientDelay,
            castSound = castSound,
            soundRadius = soundRadius,
        )

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
