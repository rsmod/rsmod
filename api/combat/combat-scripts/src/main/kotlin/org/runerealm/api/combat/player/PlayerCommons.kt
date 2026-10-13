package org.runerealm.api.combat.player

import kotlin.math.min
import org.runerealm.api.area.checker.WildernessArea
import org.runerealm.api.combat.MAGIC_ATTACK_RANGE
import org.runerealm.api.combat.MAX_ATTACK_RANGE
import org.runerealm.api.combat.commons.CombatAttack
import org.runerealm.api.combat.commons.magic.MagicSpell
import org.runerealm.api.combat.commons.styles.AttackStyle
import org.runerealm.api.combat.commons.styles.MagicAttackStyle
import org.runerealm.api.combat.commons.styles.MeleeAttackStyle
import org.runerealm.api.combat.commons.styles.RangedAttackStyle
import org.runerealm.api.combat.commons.types.AttackType
import org.runerealm.api.combat.commons.types.MeleeAttackType
import org.runerealm.api.combat.commons.types.RangedAttackType
import org.runerealm.api.combat.manager.MagicRuneManager
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.params
import org.runerealm.api.config.refs.timers
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.righthand
import org.runerealm.api.specials.SpecialAttack
import org.runerealm.api.specials.SpecialAttackRegistry
import org.runerealm.api.specials.energy.SpecialAttackEnergy
import org.runerealm.api.spells.MagicSpellRegistry
import org.runerealm.api.spells.autocast.AutocastWeapons
import org.runerealm.game.entity.PathingEntity
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.InvObj
import org.runerealm.game.type.obj.ObjTypeList

internal fun ProtectedAccess.attackRange(style: AttackStyle?): Int =
    if (autocastSpell > 0) {
        MAGIC_ATTACK_RANGE
    } else {
        weaponAttackRange(style)
    }

internal fun ProtectedAccess.weaponAttackRange(style: AttackStyle?): Int {
    var attackRange = 1

    val weapon = player.righthand
    if (weapon != null) {
        val weaponRange = ocParam(weapon, params.attackrange)
        val increase = if (style == AttackStyle.LongrangeRanged) 2 else 0
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
): CombatAttack.PlayerAttack =
    when {
        spell != null -> {
            CombatAttack.Spell(weapon, spell, defensiveCasting)
        }

        type?.isMagic == true -> {
            check(style == null || style.isMagic) {
                "Expected attack style to be magic-based: style=$style, type=$type, weapon=$weapon"
            }
            checkNotNull(weapon) {
                "Expected valid weapon for magic-staff-based attack: style=$style, type=$type"
            }
            val magicStyle = MagicAttackStyle.from(style)
            CombatAttack.Staff(weapon, magicStyle)
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

internal fun ProtectedAccess.resolveAutocastSpell(
    objTypes: ObjTypeList,
    spells: MagicSpellRegistry,
    runes: MagicRuneManager,
    autocast: AutocastWeapons,
): MagicSpell? {
    if (!autocastEnabled) {
        return null
    }
    val weapon = player.righthand ?: return null
    val spell = spells.getAutocastSpell(autocastSpell) ?: return null

    val weaponType = objTypes[weapon]

    if (spell.spellbook != spellbook) {
        mes("You can't autocast that spell with your current active spellbook.")

        autocastEnabled = false
        autocast.reset(player, weaponType)
        return null
    }

    val isValidStaff = autocast.canStaffAutocast(player, weaponType, autocastSpell)
    if (!isValidStaff) {
        return null
    }

    val canCastSpell = runes.canCastSpell(player, spell)
    if (!canCastSpell) {
        autocastEnabled = false
        autocast.reset(player, weaponType)
        return null
    }

    return spell
}

internal suspend fun ProtectedAccess.activateMeleeSpecial(
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
        val hasRequiredEnergy = energy.hasSpecialEnergy(player, special.energyInHundreds)
        if (!hasRequiredEnergy) {
            mes("You don't have enough power left.")
            return false
        }
    }

    // Official behavior: If a special attack reaches this point, the attack delay is always
    // applied. That is, even if the special fails (e.g., due to an unmet condition), the
    // interaction will either be canceled entirely or proceed with the delay.

    val reduceEnergy = special.attack(this, target, attack)
    if (reduceEnergy) {
        energy.takeSpecialEnergy(player, special.energyInHundreds)
    }
    return true
}

internal suspend fun ProtectedAccess.activateRangedSpecial(
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
        val hasRequiredEnergy = energy.hasSpecialEnergy(player, special.energyInHundreds)
        if (!hasRequiredEnergy) {
            mes("You don't have enough power left.")
            return false
        }
    }

    // Official behavior: If a special attack reaches this point, the attack delay is always
    // applied. That is, even if the special fails (e.g., due to an unmet condition), the
    // interaction will either be canceled entirely or proceed with the delay.

    val reduceEnergy = special.attack(this, target, attack)
    if (reduceEnergy) {
        energy.takeSpecialEnergy(player, special.energyInHundreds)
    }
    return true
}

internal suspend fun ProtectedAccess.activateMagicSpecial(
    target: PathingEntity,
    attack: CombatAttack.Staff,
    specials: SpecialAttackRegistry,
    energy: SpecialAttackEnergy,
): Boolean {
    val special = specials[attack.weapon] ?: return false
    if (special !is SpecialAttack.Magic) {
        return false
    }

    val specializedEnergyReq = energy.isSpecializedRequirement(special.energyInHundreds)
    if (!specializedEnergyReq) {
        val hasRequiredEnergy = energy.hasSpecialEnergy(player, special.energyInHundreds)
        if (!hasRequiredEnergy) {
            mes("You don't have enough power left.")
            return false
        }
    }

    // Official behavior: If a special attack reaches this point, the attack delay is always
    // applied. That is, even if the special fails (e.g., due to an unmet condition), the
    // interaction will either be canceled entirely or proceed with the delay.

    val reduceEnergy = special.attack(this, target, attack)
    if (reduceEnergy) {
        energy.takeSpecialEnergy(player, special.energyInHundreds)
    }
    return true
}

internal suspend fun ProtectedAccess.activateShieldSpecial(
    target: PathingEntity,
    shield: InvObj?,
    specials: SpecialAttackRegistry,
): Boolean = TODO()

internal fun ProtectedAccess.setPkVars(target: Player) {
    skullIfAggressor(target)
    pkPrey2 = pkPrey1
    pkPrey1 = target.uid

    target.pkPredator3 = target.pkPredator2
    target.pkPredator2 = target.pkPredator1
    target.pkPredator1 = player.uid

    target.lastCombat = mapClock
    target.lastCombatPvp = mapClock
    target.aggressiveNpc = null
}

/**
 * Skulls the player for [PK_SKULL_CYCLES] (20 minutes) when they attack a player in the Wilderness
 * who has not attacked them first, per the wiki.
 */
private fun ProtectedAccess.skullIfAggressor(target: Player) {
    if (!WildernessArea.contains(player.coords)) {
        return
    }
    val retaliating =
        player.pkPredator1 == target.uid ||
            player.pkPredator2 == target.uid ||
            player.pkPredator3 == target.uid
    if (retaliating) {
        return
    }
    if (player.skullIcon == null) {
        player.skullIcon = constants.skullicon_default
    }
    player.softTimer(timers.pk_skull, PK_SKULL_CYCLES)
}

internal const val PK_SKULL_CYCLES: Int = 2000
