package org.rsmod.api.combat.player

import kotlin.math.min
import org.rsmod.api.combat.commons.CombatAttack
import org.rsmod.api.combat.magic.MagicSpell
import org.rsmod.api.combat.weapon.styles.AttackStyle
import org.rsmod.api.combat.weapon.styles.MeleeAttackStyle
import org.rsmod.api.combat.weapon.styles.RangedAttackStyle
import org.rsmod.api.combat.weapon.types.AttackType
import org.rsmod.api.combat.weapon.types.MeleeAttackType
import org.rsmod.api.combat.weapon.types.RangedAttackType
import org.rsmod.api.config.refs.params
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.righthand
import org.rsmod.api.specials.SpecialAttack
import org.rsmod.api.specials.SpecialAttackRegistry
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.game.entity.PathingEntity
import org.rsmod.game.obj.InvObj

internal const val ACTIVE_COMBAT_DELAY = 8

internal const val MAX_ATTACK_RANGE = 10
internal const val MAGIC_ATTACK_RANGE = MAX_ATTACK_RANGE

internal fun ProtectedAccess.attackRange(style: AttackStyle?): Int =
    if (autoCastSpell > 0) {
        MAGIC_ATTACK_RANGE
    } else {
        weaponAttackRange(style)
    }

internal fun ProtectedAccess.weaponAttackRange(style: AttackStyle?): Int {
    var attackRange = 1

    val weapon = player.righthand
    if (weapon != null) {
        val weaponRange = ocParam(weapon, params.attackrange)
        val increase = if (style == AttackStyle.LongRangeRanged) 2 else 0
        attackRange = weaponRange + increase
    }

    check(attackRange > 0) {
        "Attack range must be greater than 0: range=$attackRange, weapon=$weapon, style=$style"
    }

    return min(MAX_ATTACK_RANGE, attackRange)
}

internal fun ProtectedAccess.resolveCombatAttack(
    weapon: InvObj?,
    type: AttackType?,
    style: AttackStyle?,
    spell: MagicSpell?,
): CombatAttack =
    when {
        spell != null -> {
            CombatAttack.Spell(weapon, spell, defensiveCasting)
        }

        type?.isMagic == true -> {
            checkNotNull(weapon) {
                "Expected valid weapon for magic-staff-based attack: style=$style, type=$type"
            }
            CombatAttack.Staff(weapon, defensiveCasting)
        }

        type?.isRanged == true -> {
            check(style == null || style.isRanged) {
                "Expected attack style to be ranged-based: style=$style, type=$type, weapon=$weapon"
            }
            checkNotNull(weapon) {
                "Expected valid weapon for ranged-based attack: style=$style, type=$type"
            }
            val rangedType = RangedAttackType.from(type)
            val rangedStyle = RangedAttackStyle.from(style)
            CombatAttack.Ranged(weapon, rangedType, rangedStyle)
        }

        else -> {
            val meleeType = MeleeAttackType.from(type)
            val meleeStyle = MeleeAttackStyle.from(style)
            CombatAttack.Melee(weapon, meleeType, meleeStyle, combatStance)
        }
    }

// TODO(combat): Multi combat areas
internal fun ProtectedAccess.inMultiCombatArea(): Boolean {
    return false
}

internal suspend fun ProtectedAccess.canPerformMeleeSpecial(
    target: PathingEntity,
    attack: CombatAttack.Melee,
    specials: SpecialAttackRegistry,
    energy: SpecialAttackEnergy,
): Boolean {
    val weapon = attack.weapon ?: return false
    val special = specials[weapon] ?: return false
    if (special !is SpecialAttack.Melee) {
        return false
    }

    val specializedEnergy = energy.isSpecializedRequirement(special.energyInHundreds)
    if (!specializedEnergy) {
        val energyReduced = energy.takeSpecialEnergyAttempt(player, special.energyInHundreds)
        if (!energyReduced) {
            mes("You don't have enough power left.")
            return false
        }
    }

    return special.attack(this, target, attack)
}

internal suspend fun ProtectedAccess.canPerformRangedSpecial(
    target: PathingEntity,
    attack: CombatAttack.Ranged,
    specials: SpecialAttackRegistry,
    energy: SpecialAttackEnergy,
): Boolean {
    val special = specials[attack.weapon] ?: return false
    if (special !is SpecialAttack.Ranged) {
        return false
    }

    val specializedEnergyReq = energy.isSpecializedRequirement(special.energyInHundreds)
    if (!specializedEnergyReq) {
        val energyReduced = energy.takeSpecialEnergyAttempt(player, special.energyInHundreds)
        if (!energyReduced) {
            mes("You don't have enough power left.")
            return false
        }
    }

    return special.attack(this, target, attack)
}

internal suspend fun ProtectedAccess.canPerformMagicSpecial(
    target: PathingEntity,
    weapon: InvObj,
    attack: CombatAttack.Staff,
    specials: SpecialAttackRegistry,
    energy: SpecialAttackEnergy,
): Boolean = TODO()

internal suspend fun ProtectedAccess.canPerformShieldSpecial(
    target: PathingEntity,
    shield: InvObj?,
    specials: SpecialAttackRegistry,
): Boolean = TODO()
