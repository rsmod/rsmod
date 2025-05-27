package org.rsmod.content.interfaces.gameframe

import org.rsmod.api.config.refs.components
import org.rsmod.api.config.refs.interfaces
import org.rsmod.game.type.comp.ComponentType

internal object StandardOverlays {
    val open: List<GameframeOverlay> =
        listOf(
            GameframeOverlay(interfaces.chatbox, components.toplevel_target_chat_container),
            GameframeOverlay(interfaces.buff_bar, components.toplevel_target_buff_bar),
            GameframeOverlay(
                interfaces.stat_boosts_hud,
                components.toplevel_target_stat_boosts_hud,
            ),
            GameframeOverlay(interfaces.pm_chat, components.toplevel_target_pm_container),
            GameframeOverlay(interfaces.hpbar_hud, components.toplevel_target_hpbar_hud),
            GameframeOverlay(interfaces.pvp_icons, components.toplevel_target_pvp_icons),
            GameframeOverlay(interfaces.orbs, components.toplevel_target_orbs),
            GameframeOverlay(interfaces.xp_drops, components.toplevel_target_xp_drops),
            GameframeOverlay(interfaces.popout, components.toplevel_target_popout),
            GameframeOverlay(interfaces.ehc_worldhop, components.toplevel_target_ehc_listener),
            GameframeOverlay(interfaces.stats, components.toplevel_target_side1),
            GameframeOverlay(interfaces.side_journal, components.toplevel_target_side2),
            GameframeOverlay(interfaces.inventory, components.toplevel_target_side3),
            GameframeOverlay(interfaces.wornitems, components.toplevel_target_side4),
            GameframeOverlay(interfaces.prayerbook, components.toplevel_target_side5),
            GameframeOverlay(interfaces.magic_spellbook, components.toplevel_target_side6),
            GameframeOverlay(interfaces.friends, components.toplevel_target_side9),
            GameframeOverlay(interfaces.account, components.toplevel_target_side8),
            GameframeOverlay(interfaces.logout, components.toplevel_target_side10),
            GameframeOverlay(interfaces.settings_side, components.toplevel_target_side11),
            GameframeOverlay(interfaces.emote, components.toplevel_target_side12),
            GameframeOverlay(interfaces.music, components.toplevel_target_side13),
            GameframeOverlay(interfaces.side_channels, components.toplevel_target_side7),
            GameframeOverlay(interfaces.combat_interface, components.toplevel_target_side0),
        )

    val move: List<ComponentType> =
        listOf(
            components.toplevel_target_chat_container,
            components.mainmodal,
            components.toplevel_target_maincrm,
            components.toplevel_target_overlay_atmosphere,
            components.toplevel_target_overlay_hud,
            components.sidemodal,
            components.toplevel_target_side0,
            components.toplevel_target_side1,
            components.toplevel_target_side2,
            components.toplevel_target_side3,
            components.toplevel_target_side4,
            components.toplevel_target_side5,
            components.toplevel_target_side6,
            components.toplevel_target_side7,
            components.toplevel_target_side8,
            components.toplevel_target_side9,
            components.toplevel_target_side10,
            components.toplevel_target_side11,
            components.toplevel_target_side12,
            components.toplevel_target_side13,
            components.toplevel_target_sidecrm,
            components.toplevel_target_pvp_icons,
            components.toplevel_target_pm_container,
            components.toplevel_target_orbs,
            components.toplevel_target_xp_drops,
            components.toplevel_target_zeah,
            components.toplevel_target_floater,
            components.toplevel_target_buff_bar,
            components.toplevel_target_stat_boosts_hud,
            components.toplevel_target_helper_content,
            components.toplevel_target_hpbar_hud,
            components.toplevel_target_popout,
            components.toplevel_target_ehc_listener,
        )
}
