package org.rsmod.content.interfaces.combat.tab

import jakarta.inject.Inject
import org.rsmod.api.config.refs.interfaces
import org.rsmod.api.config.refs.varps
import org.rsmod.api.player.righthand
import org.rsmod.api.player.ui.PlayerInterfaceUpdates
import org.rsmod.api.player.vars.boolVarp
import org.rsmod.api.player.vars.enumVarp
import org.rsmod.api.script.onIfOpen
import org.rsmod.api.script.onIfOverlayButton
import org.rsmod.api.utils.vars.VarEnumDelegate
import org.rsmod.content.interfaces.combat.tab.configs.combat_components
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.WeaponCategory
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class CombatTabScript @Inject constructor(private val objTypes: ObjTypeList) : PluginScript() {
    private var Player.styleSelection by enumVarp<StyleSelection>(varps.attackstyle)
    private var Player.autoRetaliate by boolVarp(varps.auto_retaliate)

    override fun ScriptContext.startUp() {
        onIfOpen(interfaces.combat_tab) { player.updateCombatTab() }
        onIfOverlayButton(combat_components.auto_retaliate) { player.toggleAutoRetaliate() }
        onIfOverlayButton(combat_components.style0) { player.changeStyle(StyleSelection.Style0) }
        onIfOverlayButton(combat_components.style1) { player.changeStyle(StyleSelection.Style1) }
        onIfOverlayButton(combat_components.style2) { player.changeStyle(StyleSelection.Style2) }
        onIfOverlayButton(combat_components.style3) { player.changeStyle(StyleSelection.Style3) }
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
    }

    private enum class StyleSelection(override val varValue: Int) : VarEnumDelegate {
        Style0(0),
        Style1(1),
        Style2(2),
        Style3(3),
    }
}
