package org.rsmod.api.specials

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.npc.combatPlayDefendFx
import org.rsmod.api.combat.commons.npc.queueCombatRetaliate
import org.rsmod.api.combat.commons.player.combatPlayDefendFx
import org.rsmod.api.combat.commons.player.queueCombatRetaliate
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.combat.formulas.MaxHitFormulae
import org.rsmod.api.npc.hit.modifier.NpcHitModifier
import org.rsmod.api.npc.hit.queueHit
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.interact.NpcInteractions
import org.rsmod.api.player.interact.PlayerInteractions
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.random.GameRandom
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.api.specials.weapon.SpecialAttackWeapons
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.HitType
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.ObjTypeList

public class SpecialAttackManager
@Inject
constructor(
    private val random: GameRandom,
    private val objTypes: ObjTypeList,
    private val energy: SpecialAttackEnergy,
    private val weapons: SpecialAttackWeapons,
    private val maxHits: MaxHitFormulae,
    private val npcHitModifier: NpcHitModifier,
    private val npcInteractions: NpcInteractions,
    private val playerInteractions: PlayerInteractions,
) {
    public fun hasSpecialEnergy(source: ProtectedAccess, energyInHundreds: Int): Boolean {
        return energy.hasSpecialEnergy(source.player, energyInHundreds)
    }

    public fun takeSpecialEnergy(source: ProtectedAccess, energyInHundreds: Int) {
        energy.takeSpecialEnergy(source.player, energyInHundreds)
    }

    public fun getSpecialEnergyRequirement(obj: ObjType): Int? = weapons.getSpecialEnergy(obj)

    public fun setNextAttackDelay(source: ProtectedAccess, cycles: Int) {
        source.actionDelay = source.mapClock + cycles
    }

    public fun resetCombat(source: ProtectedAccess) {
        source.stopAction()
        setNextAttackDelay(source, 0)
    }

    public fun continueCombat(source: ProtectedAccess, target: Npc) {
        source.opNpc2(target, npcInteractions)
    }

    public fun continueCombat(source: ProtectedAccess, target: Player) {
        source.opPlayer2(target, playerInteractions)
    }

    public fun rollMeleeDamage(
        source: ProtectedAccess,
        target: PathingEntity,
        attack: CombatAttack.Melee,
        accuracyBoost: Int,
        damageBoost: Int,
        hitAttackType: MeleeAttackType? = attack.type,
        hitAttackStyle: MeleeAttackStyle? = attack.style,
        blockAttackType: MeleeAttackType? = attack.type,
    ): Int {
        val successfulAccuracyRoll =
            rollMeleeAccuracy(
                source = source,
                target = target,
                percentBoost = accuracyBoost,
                hitAttackType = hitAttackType,
                hitAttackStyle = hitAttackStyle,
                blockAttackType = blockAttackType,
            )
        if (!successfulAccuracyRoll) {
            return 0
        }
        return rollMeleeMaxHit(source, target, hitAttackType, hitAttackStyle, damageBoost)
    }

    public fun rollMeleeAccuracy(
        source: ProtectedAccess,
        target: PathingEntity,
        percentBoost: Int,
        hitAttackType: MeleeAttackType?,
        hitAttackStyle: MeleeAttackStyle?,
        blockAttackType: MeleeAttackType?,
    ): Boolean {
        // TODO(combat): Accuracy formula
        return true
    }

    public fun rollMeleeMaxHit(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        percentBoost: Int,
    ): Int {
        val maxHit = calculateMeleeMaxHit(source, target, attackType, attackStyle, percentBoost)
        return random.of(0, maxHit)
    }

    public fun calculateMeleeMaxHit(
        source: ProtectedAccess,
        target: PathingEntity,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        percentBoost: Int,
    ): Int {
        val multiplier = 1 + (percentBoost / 100.0)
        return when (target) {
            is Npc -> calculateMeleeMaxHit(source, target, attackType, attackStyle, multiplier)
            is Player -> calculateMeleeMaxHit(source, target, attackType, attackStyle, multiplier)
        }
    }

    private fun calculateMeleeMaxHit(
        source: ProtectedAccess,
        target: Npc,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specMultiplier: Double,
    ): Int = maxHits.getMeleeMaxHit(source.player, target, attackType, attackStyle, specMultiplier)

    private fun calculateMeleeMaxHit(
        source: ProtectedAccess,
        target: Player,
        attackType: MeleeAttackType?,
        attackStyle: MeleeAttackStyle?,
        specMultiplier: Double,
    ): Int = TODO() // TODO(combat)

    public fun queueMeleeHit(
        source: ProtectedAccess,
        target: PathingEntity,
        damage: Int,
        delay: Int,
    ) {
        when (target) {
            is Npc -> queueMeleeHit(source, target, damage, delay)
            is Player -> queueMeleeHit(source, target, damage, delay)
        }
    }

    private fun queueMeleeHit(source: ProtectedAccess, target: Npc, damage: Int, delay: Int) {
        target.queueHit(source.player, delay, HitType.Melee, damage, npcHitModifier)
        target.combatPlayDefendFx(source.player)
        target.queueCombatRetaliate(source.player)
    }

    private fun queueMeleeHit(source: ProtectedAccess, target: Player, damage: Int, delay: Int) {
        target.queueHit(source.player, delay, HitType.Melee, damage)
        target.combatPlayDefendFx(source.player, damage, objTypes)
        target.queueCombatRetaliate(source.player)
    }
}
