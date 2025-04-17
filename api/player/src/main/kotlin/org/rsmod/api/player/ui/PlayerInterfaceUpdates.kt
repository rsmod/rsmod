package org.rsmod.api.player.ui

import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.output.ClientScripts
import org.rsmod.api.player.righthand
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.game.entity.Player
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.WeaponCategory

private var Player.combatTabWeaponStyle: Int by intVarBit(varbits.combat_weapon_category)
private var Player.combatLvlWhole: Int by intVarBit(varbits.combatlevel_transmit)
private var Player.combatLvlDecimal: Int by intVarBit(varbits.combatlevel_decimal_transmit)

public object PlayerInterfaceUpdates {
    public fun updateCombatTab(
        player: Player,
        weaponName: String?,
        categoryId: Int,
        categoryName: String,
    ) {
        player.combatTabWeaponStyle = categoryId
        player.ifSetText(components.combat_tab_title, weaponName ?: "Unarmed")
        ClientScripts.pvpIconsComLevelRange(player, player.combatLevel)
        player.ifSetText(components.combat_tab_category, "Category: $categoryName")
        ClientScripts.pvpIconsComLevelRange(player, player.combatLevel)
    }

    public fun updateCombatTab(player: Player, objTypes: ObjTypeList) {
        val righthandType = objTypes.getOrNull(player.righthand)
        val weaponCategory = WeaponCategory.getOrUnarmed(righthandType?.weaponCategory)
        updateCombatTab(player, righthandType?.name, weaponCategory.id, weaponCategory.text)
    }

    public fun updateWeaponCategoryText(player: Player, objTypes: ObjTypeList) {
        val righthandType = objTypes.getOrNull(player.righthand)
        val weaponCategory = WeaponCategory.getOrUnarmed(righthandType?.weaponCategory)
        player.ifSetText(components.combat_tab_category, "Category: ${weaponCategory.text}")
    }

    public fun updateCombatLevel(player: Player) {
        player.combatLvlWhole = player.combatLevel
        player.combatLvlDecimal = player.combatLevelDecimal
    }
}
