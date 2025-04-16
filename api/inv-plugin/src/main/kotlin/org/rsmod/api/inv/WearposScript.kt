package org.rsmod.api.inv

import jakarta.inject.Inject
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.output.MiscOutput.setPlayerOp
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.righthand
import org.rsmod.api.player.ui.PlayerInterfaceUpdates
import org.rsmod.api.player.vars.enumVarp
import org.rsmod.api.player.vars.resyncVar
import org.rsmod.api.script.advanced.onWearposChange
import org.rsmod.api.specials.SpecialAttackType
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/*
 * Note: The logic and execution order in this script are designed for emulation accuracy. While
 * this approach is not optimized for efficiency and includes redundant operations, it does not have
 * a measurable impact on real-world performance.
 */
public class WearposScript @Inject constructor(private val objTypes: ObjTypeList) : PluginScript() {
    private var Player.specialType by enumVarp<SpecialAttackType>(varps.sa_attack)

    private val Player.weaponSpecialActive: Boolean
        get() = specialType == SpecialAttackType.Weapon

    private val Player.shieldSpecialActive: Boolean
        get() = specialType == SpecialAttackType.Shield

    override fun ScriptContext.startUp() {
        onWearposChange { player.disableSpecialAttack(wearpos) }

        // Note: At this point, the official game sends a few content-specific vars. While we
        // generally aim for emulation accuracy, sending these vars here - between more
        // "engine"-esque logic - would hurt maintainability. Handling them would require either:
        // 1) Introducing a new event and publishing it inside an `onWearposChange` script here, or
        // 2) Hardcoding conditions for unrelated vars that fall outside this script's scope.
        //
        // For documentation purposes, the vars that would be sent here are:
        // varp 728, 454, 518, 491, 3413, 3160, 3158, and 3159.
        //
        // - varp 454: Treasure Trail-related
        // - varps 491, 3413: Runecrafting altar-related
        // - varps 3158–3160: "Buff bar" cs2 script-related
        // - The rest are currently unknown.

        onWearposChange { player.sendSoundAndPlayerOp(objType) }
        onWearposChange { player.validateWeaponVars(wearpos) }
    }

    private fun Player.disableSpecialAttack(wearpos: Wearpos) {
        val disableWeaponSpec = wearpos == Wearpos.RightHand && weaponSpecialActive
        val disableShieldSpec = wearpos == Wearpos.LeftHand && shieldSpecialActive
        if (disableWeaponSpec || disableShieldSpec) {
            specialType = SpecialAttackType.None
        }
    }

    private fun Player.sendSoundAndPlayerOp(type: UnpackedObjType) {
        val sound = type.paramOrNull(params.equipment_sound)
        sound?.let(::soundSynth)

        val righthand = objTypes.getOrNull(this.righthand)
        val playerOp5 = righthand?.paramOrNull(params.player_op5_text)
        setPlayerOp(this, slot = 5, op = playerOp5, priority = playerOp5 != null)
    }

    private fun Player.validateWeaponVars(wearpos: Wearpos) {
        if (wearpos != Wearpos.RightHand && wearpos != Wearpos.LeftHand) {
            return
        }

        val righthand = objTypes.getOrNull(this.righthand)
        val isTwoHanded = righthand?.isTwoHanded() ?: false
        val isRighthand = wearpos == Wearpos.RightHand

        // These two conditions aren't the most logical, however, they are done this way for
        // emulation purposes. Clearing the special attack type is not even required at this
        // point, however it is done in the official game.
        val updateCombatVars = isRighthand || !isTwoHanded
        val clearPendingSpec = isRighthand && !isTwoHanded

        if (updateCombatVars) {
            resyncVar(varps.com_mode)
        }

        if (clearPendingSpec) {
            specialType = SpecialAttackType.None
        }

        if (updateCombatVars) {
            PlayerInterfaceUpdates.updateCombatLevel(this)
            PlayerInterfaceUpdates.updateCombatTab(this, objTypes)
        }
    }

    private fun UnpackedObjType.isTwoHanded(): Boolean =
        wearpos2 == Wearpos.LeftHand.slot || wearpos3 == Wearpos.LeftHand.slot
}
