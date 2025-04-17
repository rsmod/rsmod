package org.rsmod.content.interfaces.gameframe.util

import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.game.type.comp.ComponentType
import org.rsmod.game.type.interf.InterfaceType

object StandardOverlays {
    fun create(): List<Pair<InterfaceType, ComponentType>> =
        listOf(
            interfaces.popout to components.toplevel_target_popout,
            interfaces.stats to components.toplevel_target_stats,
            interfaces.emote to components.toplevel_target_emote,
            interfaces.orbs to components.toplevel_target_orbs,
            interfaces.combat_interface to components.toplevel_target_combat,
            interfaces.chatbox to components.toplevel_target_chatbox,
            interfaces.xp_drops to components.toplevel_target_xpdrops,
            interfaces.magic_spellbook to components.toplevel_target_magicspellbook,
            interfaces.pm_chat to components.toplevel_target_pmchat,
            interfaces.wornitems to components.toplevel_target_wornitems,
            interfaces.side_channels to components.toplevel_target_sidechannels,
            interfaces.settings_side to components.toplevel_target_settingsside,
            interfaces.side_journal to components.toplevel_target_sidejournal,
            interfaces.inventory to components.toplevel_target_inventory,
            interfaces.prayerbook to components.toplevel_target_prayerbook,
            interfaces.friends to components.toplevel_target_friends,
            interfaces.account to components.toplevel_target_account,
            interfaces.worldswitcher to components.toplevel_target_worldswitcher,
            interfaces.hpbar_hud to components.toplevel_target_hpbarhud,
            interfaces.music to components.toplevel_target_music,
        )
}
