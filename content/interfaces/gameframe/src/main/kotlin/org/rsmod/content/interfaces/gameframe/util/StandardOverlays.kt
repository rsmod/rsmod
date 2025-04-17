package org.rsmod.content.interfaces.gameframe.util

import org.rsmod.api.config.refs.BaseComponents
import org.rsmod.api.config.refs.BaseInterfaces
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.InterfaceType

object StandardOverlays {
    fun create(): List<Pair<InterfaceType, ComponentType>> =
        listOf(
            BaseInterfaces.steam_side_panel to BaseComponents.popout,
            BaseInterfaces.skills_tab to BaseComponents.skills_tab_target,
            BaseInterfaces.emote_tab to BaseComponents.emote_tab_target,
            BaseInterfaces.orbs to BaseComponents.orbs_target,
            BaseInterfaces.combat_tab to BaseComponents.combat_tab_target,
            BaseInterfaces.chat to BaseComponents.chat_target,
            BaseInterfaces.experience_drops_window to BaseComponents.xp_drops_target,
            BaseInterfaces.spellbook_tab to BaseComponents.spellbook_tab_target,
            BaseInterfaces.private_chat to BaseComponents.private_chat_target,
            BaseInterfaces.equipment_tab to BaseComponents.equipment_tab_target,
            BaseInterfaces.chat_header to BaseComponents.chat_header_target,
            BaseInterfaces.settings_tab to BaseComponents.settings_tab_target,
            BaseInterfaces.journal_header_tab to BaseComponents.journal_header_tab_target,
            BaseInterfaces.inventory_tab to BaseComponents.inventory_tab_target,
            BaseInterfaces.prayer_tab to BaseComponents.prayer_tab_target,
            BaseInterfaces.friend_list_tab to BaseComponents.friend_list_tab_target,
            BaseInterfaces.account_management_tab to BaseComponents.account_management_tab_target,
            BaseInterfaces.world_switcher to BaseComponents.world_switcher_target,
            BaseInterfaces.hp_hud to BaseComponents.hp_hud_target,
            BaseInterfaces.music_tab to BaseComponents.music_tab_target,
        )
}
