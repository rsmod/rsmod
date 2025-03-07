package org.rsmod.content.interfaces.combat.tab

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatStance
import org.rsmod.api.combat.magic.autocast.AutocastWeapons
import org.rsmod.api.combat.weapon.styles.AttackStyles
import org.rsmod.api.combat.weapon.styles.MeleeAttackStyle
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.righthand
import org.rsmod.api.player.ui.PlayerInterfaceUpdates
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.vars.boolVarBit
import org.rsmod.api.player.vars.boolVarp
import org.rsmod.api.player.vars.enumVarp
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.api.script.advanced.onWearposChange
import org.rsmod.api.script.onIfOpen
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.api.script.onPlayerQueue
import org.rsmod.api.script.onPlayerQueueWithArgs
import org.rsmod.api.specials.SpecialAttack
import org.rsmod.api.specials.SpecialAttackRegistry
import org.rsmod.api.specials.SpecialAttackType
import org.rsmod.api.specials.energy.SpecialAttackEnergy
import org.rsmod.content.interfaces.combat.tab.configs.combat_components
import org.rsmod.content.interfaces.combat.tab.configs.combat_enums
import org.rsmod.content.interfaces.combat.tab.configs.combat_queues
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.WeaponCategory
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.game.type.util.EnumTypeNonNullMap
import org.rsmod.game.type.varbit.VarBitType
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext
import org.rsmod.utils.bits.withBits

/*
 * Note: The logic and execution order in this script are designed for emulation accuracy. While
 * this approach is not optimized for efficiency and includes redundant operations, it should not
 * have a big impact on real-world performance.
 */
class CombatTabScript
@Inject
constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val enumResolver: EnumTypeMapResolver,
    private val weaponStyles: AttackStyles,
    private val autocast: AutocastWeapons,
    private val energy: SpecialAttackEnergy,
    private val specialReg: SpecialAttackRegistry,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    private var Player.combatStance by enumVarp<CombatStance>(varps.attackstyle)
    private var Player.meleeStyle by enumVarp<MeleeAttackStyle>(varps.attackstyle_melee)
    private var Player.autoRetaliate by boolVarp(varps.auto_retaliate)
    private var Player.specialType by enumVarp<SpecialAttackType>(varps.sa_type)

    private var Player.autocastEnabled by boolVarBit(varbits.autocast_enabled)
    private var Player.autocastSpell by intVarBit(varbits.autocast_spell)
    private var Player.defensiveCasting by boolVarBit(varbits.defensive_casting_mode)

    private lateinit var stanceSaveVarBits: EnumTypeNonNullMap<Int, VarBitType>

    override fun ScriptContext.startUp() {
        stanceSaveVarBits = enumResolver[combat_enums.weapons_last_stance].filterValuesNotNull()

        onIfOpen(interfaces.combat_tab) { player.updateCombatTab() }
        onWearposChange { player.onWearposChange(wearpos) }

        onIfOverlayButton(combat_components.auto_retaliate) { player.selectAutoRetaliate() }

        onIfOverlayButton(combat_components.stance1) { player.selectStance(CombatStance.Stance1) }
        onIfOverlayButton(combat_components.stance2) { player.selectStance(CombatStance.Stance2) }
        onIfOverlayButton(combat_components.stance3) { player.selectStance(CombatStance.Stance3) }
        onIfOverlayButton(combat_components.stance4) { player.selectStance(CombatStance.Stance4) }
        onPlayerQueueWithArgs(combat_queues.attackstyle_change) { player.setStance(it.args) }

        onIfOverlayButton(combat_components.special_attack) { player.toggleSpecialAttack() }
        onIfOverlayButton(combat_components.special_attack_orb) { player.toggleSpecialAttack() }
        onPlayerQueue(combat_queues.sa_instant_spec) { activateInstantSpecial() }
    }

    private fun Player.updateCombatTab() {
        PlayerInterfaceUpdates.updateCombatTab(this, objTypes)
    }

    private fun Player.onWearposChange(wearpos: Wearpos) {
        if (wearpos == Wearpos.RightHand || wearpos == Wearpos.LeftHand) {
            loadSavedWeaponStance()
            loadSavedMagicAutocast()
            validateStanceStyle()
            PlayerInterfaceUpdates.updateCombatLevel(this)
        }
    }

    private fun Player.loadSavedWeaponStance() {
        val weaponType = righthand?.let(objTypes::get)
        val weaponCategory = WeaponCategory.getOrUnarmed(weaponType?.weaponCategory)

        val varbit = stanceSaveVarBits.getOrNull(weaponCategory.id)
        if (varbit != null) {
            val savedStanceVar = vars[varbit]

            // The null fallback means any new weapon categories being worn will default to
            // `Stance1` (usually top-left selection). This is the official behavior when
            // wielding new weapon types.
            val stance = CombatStance[savedStanceVar] ?: CombatStance.Stance1
            combatStance = stance
        }
    }

    private fun Player.loadSavedMagicAutocast() {
        val weaponType = righthand?.let(objTypes::get)
        val weaponCategory = WeaponCategory.getOrUnarmed(weaponType?.weaponCategory)
        val autocastVarBits = autocast.getVarBits(weaponCategory)

        if (weaponType == null || autocastVarBits == null) {
            autocastEnabled = false
            autocastSpell = 0
            defensiveCasting = false
            return
        }

        val savedAutocastId = vars[autocastVarBits.autocastId]
        if (savedAutocastId == 0) {
            autocastEnabled = false
            autocastSpell = 0
            defensiveCasting = false
            return
        }

        // It is worth noting that as of writing this logic, the official game does _not_ check
        // that the spell's magic spellbook param matches the player's current magic spellbook
        // when switching weapons.

        val canAutocast = autocast.canAutocast(this, weaponType, savedAutocastId)
        if (!canAutocast) {
            autocast.reset(this, autocastVarBits)

            autocastEnabled = false
            autocastSpell = 0
            defensiveCasting = false
            return
        }

        val savedDefensiveCast = vars[autocastVarBits.defensiveCast] != 0
        autocastSpell = savedAutocastId
        defensiveCasting = savedDefensiveCast
        autocastEnabled = autocastSpell != 0
    }

    private fun Player.validateStanceStyle() {
        val weaponType = righthand?.let(objTypes::get)
        val startStance = combatStance

        val weaponStyle = weaponStyles.resolve(weaponType, startStance.varValue)
        val validatedStance = if (weaponStyle == null) CombatStance.Stance1 else startStance
        this.combatStance = validatedStance

        val meleeStyle = MeleeAttackStyle.from(weaponStyle)
        if (meleeStyle != null) {
            this.meleeStyle = meleeStyle
        }
    }

    private fun Player.selectAutoRetaliate() {
        ifClose(eventBus)

        if (isAccessProtected) {
            // Unlike changing attackstyle, this does not queue the toggle; the click is
            // simply discarded.
            return
        }

        autoRetaliate = !autoRetaliate
    }

    private fun Player.selectStance(stance: CombatStance) {
        ifClose(eventBus)

        if (isAccessProtected) {
            clearQueue(combat_queues.attackstyle_change)
            queue(combat_queues.attackstyle_change, 1, stance)
            return
        }

        setStance(stance)
    }

    private fun Player.setStance(stance: CombatStance) {
        setWeaponStance(stance)
        validateChangedStanceStyle()
        saveCurrentStanceStyle()
    }

    private fun Player.setWeaponStance(stance: CombatStance) {
        combatStance = stance
        PlayerInterfaceUpdates.updateWeaponCategoryText(this, objTypes)
    }

    private fun Player.validateChangedStanceStyle() {
        val weaponType = righthand?.let(objTypes::get)
        val startStance = combatStance

        val attackStyle = weaponStyles.resolve(weaponType, startStance.varValue)
        val validated = if (attackStyle == null) CombatStance.Stance1 else startStance
        setWeaponStance(validated)

        // Subtle difference with other "stance style validation" function is that there is
        // an explicit equals condition based on current `meleeStyle`.
        val meleeStyle = MeleeAttackStyle.from(attackStyle)
        if (meleeStyle != null && this.meleeStyle != meleeStyle) {
            this.meleeStyle = meleeStyle
        }
    }

    private fun Player.saveCurrentStanceStyle() {
        val weaponType = righthand?.let(objTypes::get)
        val weaponCategory = WeaponCategory.getOrUnarmed(weaponType?.weaponCategory)

        val varbit = stanceSaveVarBits.getOrNull(weaponCategory.id)
        if (varbit != null) {
            val packed = vars[varbit.baseVar].withBits(varbit.bits, combatStance.varValue)
            vars.backing[varbit.baseVar.id] = packed
        }
    }

    private fun Player.toggleSpecialAttack() {
        when (specialType) {
            SpecialAttackType.None -> enableSpecialAttack()
            SpecialAttackType.Weapon -> resetSpecialType()
            SpecialAttackType.Shield -> {
                // Shield specials can only be reset by reactivating them the same way they
                // were enabled. For example, re-selecting the "Activate" option on a dragonfire
                // shield. They cannot be disabled via the special attack orb or the attack tab
                // special bar.
            }
        }
    }

    private fun Player.enableSpecialAttack() {
        val righthand = righthand ?: return
        when (specialReg[righthand]) {
            is SpecialAttack.Combat -> activateCombatSpecial()
            is SpecialAttack.Instant -> attemptInstantSpecial()
            null -> {
                resetSpecialType()
                mes("This weapon does not have a special attack.")
            }
        }
    }

    private fun Player.activateCombatSpecial() {
        specialType = SpecialAttackType.Weapon
    }

    private fun Player.attemptInstantSpecial() {
        resetSpecialType()

        if (combat_queues.sa_instant_spec in queueList) {
            return
        }

        ifClose(eventBus)
        val activated = protectedAccess.launch(this) { activateInstantSpecial() }
        if (!activated) {
            strongQueue(combat_queues.sa_instant_spec, 1)
        }
    }

    private suspend fun ProtectedAccess.activateInstantSpecial() {
        val righthand = player.righthand ?: return
        val special = specialReg[righthand]
        if (special !is SpecialAttack.Instant) {
            return
        }

        val specializedEnergyReq = energy.isSpecializedRequirement(special.energyInHundreds)
        if (!specializedEnergyReq) {
            val energyReduced = energy.takeSpecialEnergyAttempt(player, special.energyInHundreds)
            if (!energyReduced) {
                mes("You don't have enough power left.")
                return
            }
        }

        special.activate(this)
    }

    private fun Player.resetSpecialType() {
        specialType = SpecialAttackType.None
    }
}
