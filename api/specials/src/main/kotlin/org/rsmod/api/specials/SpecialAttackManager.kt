package org.rsmod.api.specials

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.commons.styles.MeleeAttackStyle
import org.rsmod.api.combat.commons.types.MeleeAttackType
import org.rsmod.api.npc.hit.modifier.HitModifierNpc
import org.rsmod.api.npc.hit.queueHit
import org.rsmod.api.player.hit.queueHit
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.api.specials.weapon.SpecialAttackWeapons
import org.rsmod.game.entity.Npc
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.entity.Player
import org.rsmod.game.hit.HitType
import org.rsmod.game.type.obj.ObjType

public class SpecialAttackManager
@Inject
constructor(
    private val energy: SpecialAttackEnergy,
    private val weapons: SpecialAttackWeapons,
    private val npcHitModifier: HitModifierNpc,
) {
    public fun setNextAttackDelay(source: ProtectedAccess, cycles: Int) {
        source.actionDelay = source.mapClock + cycles
    }

    public fun resetCombat(source: ProtectedAccess) {
        source.stopAction()
        setNextAttackDelay(source, 0)
    }

    public fun continueCombat(source: ProtectedAccess, target: Npc) {
        source.opNpc2(target)
    }

    public fun continueCombat(source: ProtectedAccess, target: Player) {
        // TODO(combat): opplayer2
    }

    public fun rollMeleeDamage(
        source: ProtectedAccess,
        target: PathingEntity,
        attack: CombatAttack.Melee,
        accuracyBoost: Int,
        damageBoost: Int,
        blockAttackType: MeleeAttackType? = attack.type,
        hitAttackType: MeleeAttackType? = attack.type,
        hitAttackStyle: MeleeAttackStyle? = attack.style,
    ): Int =
        rollMeleeDamage(
            source,
            target,
            blockAttackType,
            hitAttackType,
            hitAttackStyle,
            accuracyBoost,
            damageBoost,
        )

    private fun rollMeleeDamage(
        source: ProtectedAccess,
        target: PathingEntity,
        blockAttackType: MeleeAttackType?,
        hitAttackType: MeleeAttackType?,
        hitAttackStyle: MeleeAttackStyle?,
        accuracyBoost: Int,
        damageBoost: Int,
    ): Int {
        if (!rollMeleeAccuracy(source, target, accuracyBoost, blockAttackType)) {
            return 0
        }
        return rollMeleeMaxHit(source, target, damageBoost)
    }

    public fun rollMeleeAccuracy(
        source: ProtectedAccess,
        target: PathingEntity,
        percentBoost: Int,
        rollDefenceAgainst: MeleeAttackType?,
    ): Boolean {
        // TODO(combat): Accuracy formula
        return true
    }

    public fun rollMeleeMaxHit(
        source: ProtectedAccess,
        target: PathingEntity,
        percentBoost: Int,
    ): Int {
        // TODO(combat): Damage formula
        return 0
    }

    public fun queueMeleeHit(
        source: ProtectedAccess,
        target: PathingEntity,
        damage: Int,
        delay: Int,
    ) {
        when (target) {
            is Npc -> target.queueHit(source.player, delay, HitType.Melee, damage, npcHitModifier)
            is Player -> target.queueHit(source.player, delay, HitType.Melee, damage)
        }
    }

    public fun hasSpecialEnergy(source: ProtectedAccess, energyInHundreds: Int): Boolean {
        return energy.hasSpecialEnergy(source.player, energyInHundreds)
    }

    public fun takeSpecialEnergy(source: ProtectedAccess, energyInHundreds: Int) {
        energy.takeSpecialEnergy(source.player, energyInHundreds)
    }

    public fun getSpecialEnergyRequirement(obj: ObjType): Int? = weapons.getSpecialEnergy(obj)
}
