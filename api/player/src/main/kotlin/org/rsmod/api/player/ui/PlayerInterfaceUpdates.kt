package org.rsmod.api.player.ui

import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.output.ClientScripts.pvpIconsComLevelRange
import org.rsmod.api.player.vars.intVarp
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.WeaponCategory

private var Player.combatTabWeaponStyle: Int by intVarp(varbits.combat_tab_weapon_style_type)

public object PlayerInterfaceUpdates {
    public fun updateCombatTab(
        player: Player,
        weaponName: String?,
        categoryId: Int,
        categoryName: String,
    ) {
        player.combatTabWeaponStyle = categoryId
        player.ifSetText(components.combat_tab_weapon_name, weaponName ?: "Unarmed")
        pvpIconsComLevelRange(player, player.combatLevel)
        player.ifSetText(components.combat_tab_weapon_category, "Category: $categoryName")
        pvpIconsComLevelRange(player, player.combatLevel)
    }

    public fun updateCombatTab(
        player: Player,
        weaponName: String?,
        weaponCategory: WeaponCategory,
    ): Unit = updateCombatTab(player, weaponName, weaponCategory.id, weaponCategory.text)
}
