package org.rsmod.plugins.content.gameframe

import org.rsmod.game.event.impl.StatLevelUp
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.appearance.Equipment
import org.rsmod.plugins.api.model.mob.player.setVarp
import org.rsmod.plugins.api.model.mob.player.toggleVarp
import org.rsmod.plugins.api.model.stat.Stats
import org.rsmod.plugins.api.model.stat.combatLevel
import org.rsmod.plugins.api.model.ui.setComponentText
import org.rsmod.plugins.api.onButton
import org.rsmod.plugins.api.onEquip
import org.rsmod.plugins.api.onLogin

val combatLevelText = component("combat_level_text")
val combatLevelStats = arrayOf(
    Stats.Attack,
    Stats.Defence,
    Stats.Strength,
    Stats.Hitpoints,
    Stats.Prayer,
    Stats.Ranged,
    Stats.Magic
)

val weaponText = component("weapon_text")
val weaponVarbit = varbit("weapon_type")

val autoRetaliateButton = component("auto_retaliate_button")
val autoRetaliateVarp = varp("auto_retaliate")

val attackStyleButtons = arrayOf(
    component("attack_style_0_button"),
    component("attack_style_1_button"),
    component("attack_style_2_button"),
    component("attack_style_3_button")
)
val attackStyleVarp = varp("attack_style")

onButton(autoRetaliateButton) {
    player.toggleVarp(autoRetaliateVarp)
}

attackStyleButtons.forEachIndexed { index, button ->
    onButton(button) {
        player.setVarp(attackStyleVarp, index)
    }
}

onLogin {
    val weaponName = player.weaponName()
    player.setComponentText(weaponText, weaponName)
    player.updateCombatLevel()
}

onEquip(Equipment.WEAPON) {
    player.setComponentText(weaponText, item.name)
}

onEvent<StatLevelUp>()
    .where { key in combatLevelStats }
    .then { player.updateCombatLevel() }

fun Player.weaponName(): String {
    val item = equipment[Equipment.WEAPON] ?: return "Unarmed"
    return item.name
}

fun Player.updateCombatLevel() {
    val combatLevel = stats.combatLevel()
    setComponentText(combatLevelText, "Combat Lvl: $combatLevel")
}
