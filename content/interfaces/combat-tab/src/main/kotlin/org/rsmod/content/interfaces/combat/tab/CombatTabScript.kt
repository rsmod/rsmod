package org.rsmod.content.interfaces.combat.tab

import jakarta.inject.Inject
import org.rsmod.api.combat.commons.CombatStance
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.protect.ProtectedAccessLauncher
import org.rsmod.api.player.righthand
import org.rsmod.api.player.ui.PlayerInterfaceUpdates
import org.rsmod.api.player.ui.ifClose
import org.rsmod.api.player.vars.boolVarp
import org.rsmod.api.player.vars.enumVarp
import org.rsmod.api.script.advanced.onWearposChange
import org.rsmod.api.script.onIfOpen
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.api.script.onPlayerQueue
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

class CombatTabScript
@Inject
constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val enumResolver: EnumTypeMapResolver,
    private val energy: SpecialAttackEnergy,
    private val specialReg: SpecialAttackRegistry,
    private val protectedAccess: ProtectedAccessLauncher,
) : PluginScript() {
    private var Player.combatStance by enumVarp<CombatStance>(varps.attackstyle)
    private var Player.autoRetaliate by boolVarp(varps.auto_retaliate)
    private var Player.specialType by enumVarp<SpecialAttackType>(varps.sa_type)

    private lateinit var stanceSaveVarbits: EnumTypeNonNullMap<Int, VarBitType>

    override fun ScriptContext.startUp() {
        stanceSaveVarbits = enumResolver[combat_enums.weapons_last_stance].filterValuesNotNull()

        onIfOpen(interfaces.combat_tab) { player.updateCombatTab() }
        onWearposChange { player.onWearposChange(wearpos) }

        onIfOverlayButton(combat_components.auto_retaliate) { player.toggleAutoRetaliate() }

        onIfOverlayButton(combat_components.stance1) { player.changeStance(CombatStance.Stance1) }
        onIfOverlayButton(combat_components.stance2) { player.changeStance(CombatStance.Stance2) }
        onIfOverlayButton(combat_components.stance3) { player.changeStance(CombatStance.Stance3) }
        onIfOverlayButton(combat_components.stance4) { player.changeStance(CombatStance.Stance4) }

        onIfOverlayButton(combat_components.special_attack) { player.toggleSpecialAttack() }
        onIfOverlayButton(combat_components.special_attack_orb) { player.toggleSpecialAttack() }
        onPlayerQueue(combat_queues.sa_instant_spec) { activateInstantSpecial() }
    }

    private fun Player.updateCombatTab() {
        val weaponType = righthand?.let(objTypes::get)
        if (weaponType == null) {
            PlayerInterfaceUpdates.updateCombatTab(this, null, WeaponCategory.Unarmed)
            return
        }
        val category = WeaponCategory[weaponType.weaponCategory] ?: WeaponCategory.Unarmed
        PlayerInterfaceUpdates.updateCombatTab(this, weaponType.name, category)
    }

    private fun Player.onWearposChange(wearpos: Wearpos) {
        if (wearpos == Wearpos.RightHand) {
            loadWeaponStance()

            // TODO: Verify if wearpos change on righthand should reset shield special type.
            //  Similarly, verify what happens with shield special type on lefthand wearpos change.
            resetSpecialType()
        }
    }

    private fun Player.loadWeaponStance() {
        val weaponType = righthand?.let(objTypes::get)
        val category = WeaponCategory.getOrUnarmed(weaponType?.weaponCategory)

        val varbit = stanceSaveVarbits.getOrNull(category.id)
        if (varbit != null) {
            val savedStanceVar = vars[varbit]

            // The null fallback means any new weapon categories being worn will default to
            // `Stance1` (usually top-left selection). This is the official behavior when
            // wielding new weapon types.
            val stance = CombatStance[savedStanceVar] ?: CombatStance.Stance1
            combatStance = stance
        }
    }

    private fun Player.resetSpecialType() {
        specialType = SpecialAttackType.None
    }

    private fun Player.toggleAutoRetaliate() {
        autoRetaliate = !autoRetaliate
    }

    private fun Player.changeStance(stance: CombatStance) {
        combatStance = stance
        saveWeaponStance(stance)
    }

    private fun Player.saveWeaponStance(stance: CombatStance) {
        val weaponType = righthand?.let(objTypes::get)
        val category = WeaponCategory.getOrUnarmed(weaponType?.weaponCategory)

        val varbit = stanceSaveVarbits.getOrNull(category.id)
        if (varbit != null) {
            val packed = vars[varbit.baseVar].withBits(varbit.bits, stance.varValue)
            vars.backing[varbit.baseVar.id] = packed
        }
    }

    private fun Player.toggleSpecialAttack() {
        when (specialType) {
            SpecialAttackType.None -> enableSpecialAttack()
            SpecialAttackType.Weapon -> resetSpecialType()
            SpecialAttackType.Shield -> {
                // TODO: Was informed that "shield" specials (such as Dragonfire shield) _cannot_
                //  be reset by toggling the special attack bar/orb. This may not be true anymore,
                //  but was the case at some point. Requires verification.
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
            // Note: Not certain if this should queue with the `SpecialAttack.Instant` reference
            // from the `enableSpecialAttack` branch. At the moment, [activateInstantSpecial] will
            // find the _current_ weapon's special attack as opposed to what it was when this
            // [attemptInstantSpecial] function was called. Would need a way to test how this
            // edge case behaves. (Not sure if it's possible)
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
}
