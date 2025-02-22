package org.rsmod.content.interfaces.combat.tab

import jakarta.inject.Inject
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.righthand
import org.rsmod.api.player.ui.PlayerInterfaceUpdates
import org.rsmod.api.player.vars.boolVarp
import org.rsmod.api.player.vars.enumVarp
import org.rsmod.api.script.advanced.onWearposChange
import org.rsmod.api.script.onIfOpen
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.api.utils.vars.VarEnumDelegate
import org.rsmod.content.interfaces.combat.tab.configs.combat_components
import org.rsmod.content.interfaces.combat.tab.configs.combat_enums
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
constructor(private val objTypes: ObjTypeList, private val enumResolver: EnumTypeMapResolver) :
    PluginScript() {
    private var Player.styleSelection by enumVarp<StyleSelection>(varps.attackstyle)
    private var Player.autoRetaliate by boolVarp(varps.auto_retaliate)

    private lateinit var styleSaveVarbits: EnumTypeNonNullMap<Int, VarBitType>

    override fun ScriptContext.startUp() {
        styleSaveVarbits = enumResolver[combat_enums.weapons_last_style].filterValuesNotNull()

        onIfOpen(interfaces.combat_tab) { player.updateCombatTab() }
        onIfOverlayButton(combat_components.auto_retaliate) { player.toggleAutoRetaliate() }
        onIfOverlayButton(combat_components.style0) { player.changeStyle(StyleSelection.Style0) }
        onIfOverlayButton(combat_components.style1) { player.changeStyle(StyleSelection.Style1) }
        onIfOverlayButton(combat_components.style2) { player.changeStyle(StyleSelection.Style2) }
        onIfOverlayButton(combat_components.style3) { player.changeStyle(StyleSelection.Style3) }
        onWearposChange { player.onWearposChange(primaryWearpos) }
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

    private fun Player.toggleAutoRetaliate() {
        autoRetaliate = !autoRetaliate
    }

    private fun Player.changeStyle(style: StyleSelection) {
        styleSelection = style
        saveWeaponStyle(style)
    }

    private fun Player.saveWeaponStyle(style: StyleSelection) {
        val weaponType = righthand?.let(objTypes::get)
        val category = WeaponCategory.getOrUnarmed(weaponType?.weaponCategory)

        val varbit = styleSaveVarbits.getOrNull(category.id)
        if (varbit != null) {
            val packed = vars[varbit.baseVar].withBits(varbit.bits, style.varValue)
            vars.backing[varbit.baseVar.id] = packed
        }
    }

    private fun Player.onWearposChange(wearpos: Wearpos) {
        if (wearpos == Wearpos.RightHand) {
            loadWeaponStyle()
        }
    }

    private fun Player.loadWeaponStyle() {
        val weaponType = righthand?.let(objTypes::get)
        val category = WeaponCategory.getOrUnarmed(weaponType?.weaponCategory)

        val varbit = styleSaveVarbits.getOrNull(category.id)
        if (varbit != null) {
            val savedStyleVar = vars[varbit]

            // The `Style0` fallback means any new weapon categories being worn will default to
            // "style0" (usually top-left style). This is the official behavior when wielding new
            // weapon types.
            val selection = StyleSelection[savedStyleVar] ?: StyleSelection.Style0
            styleSelection = selection
        }
    }

    private enum class StyleSelection(override val varValue: Int) : VarEnumDelegate {
        Style0(0),
        Style1(1),
        Style2(2),
        Style3(3);

        companion object {
            operator fun get(varValue: Int): StyleSelection? =
                when (varValue) {
                    Style0.varValue -> Style0
                    Style1.varValue -> Style1
                    Style2.varValue -> Style2
                    Style3.varValue -> Style3
                    else -> null
                }
        }
    }
}
