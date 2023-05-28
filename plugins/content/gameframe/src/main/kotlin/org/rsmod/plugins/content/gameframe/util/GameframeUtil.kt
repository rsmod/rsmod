package org.rsmod.plugins.content.gameframe.util

import org.rsmod.plugins.api.attack_tab
import org.rsmod.plugins.api.chatbox
import org.rsmod.plugins.api.chatbox_username
import org.rsmod.plugins.api.clan_tab
import org.rsmod.plugins.api.component
import org.rsmod.plugins.api.emotes_tab
import org.rsmod.plugins.api.equipment_tab
import org.rsmod.plugins.api.gameframe_target_attack
import org.rsmod.plugins.api.gameframe_target_chatbox
import org.rsmod.plugins.api.gameframe_target_clan
import org.rsmod.plugins.api.gameframe_target_emotes
import org.rsmod.plugins.api.gameframe_target_equipment
import org.rsmod.plugins.api.gameframe_target_hud
import org.rsmod.plugins.api.gameframe_target_inv
import org.rsmod.plugins.api.gameframe_target_logout
import org.rsmod.plugins.api.gameframe_target_management
import org.rsmod.plugins.api.gameframe_target_minimap
import org.rsmod.plugins.api.gameframe_target_music
import org.rsmod.plugins.api.gameframe_target_prayer
import org.rsmod.plugins.api.gameframe_target_pvp
import org.rsmod.plugins.api.gameframe_target_quests
import org.rsmod.plugins.api.gameframe_target_settings
import org.rsmod.plugins.api.gameframe_target_skills
import org.rsmod.plugins.api.gameframe_target_social
import org.rsmod.plugins.api.gameframe_target_spells
import org.rsmod.plugins.api.gameframe_target_username
import org.rsmod.plugins.api.gameframe_target_xp
import org.rsmod.plugins.api.health_hud
import org.rsmod.plugins.api.interf
import org.rsmod.plugins.api.inventory_tab
import org.rsmod.plugins.api.logout_tab
import org.rsmod.plugins.api.management_tab
import org.rsmod.plugins.api.minimap
import org.rsmod.plugins.api.music_tab
import org.rsmod.plugins.api.prayer_tab
import org.rsmod.plugins.api.pvp_skull
import org.rsmod.plugins.api.quest_tab
import org.rsmod.plugins.api.settings_tab
import org.rsmod.plugins.api.skills_tab
import org.rsmod.plugins.api.social_tab
import org.rsmod.plugins.api.spellbook_tab
import org.rsmod.plugins.api.xp_counter
import org.rsmod.plugins.cache.config.enums.EnumType
import org.rsmod.plugins.types.NamedComponent
import org.rsmod.plugins.types.NamedInterface

public object GameframeUtil {

    public fun buildMappings(
        mappingsEnum: EnumType<NamedComponent, NamedComponent>
    ): Map<NamedComponent, NamedComponent> = mappingsEnum.associate { it.key to it.value }

    public fun standardOverlays(): List<Pair<NamedInterface, NamedComponent>> = listOf(
        interf.chatbox to component.gameframe_target_chatbox,
        interf.chatbox_username to component.gameframe_target_username,
        interf.pvp_skull to component.gameframe_target_pvp,
        interf.health_hud to component.gameframe_target_hud,
        interf.minimap to component.gameframe_target_minimap,
        interf.xp_counter to component.gameframe_target_xp,
        interf.skills_tab to component.gameframe_target_skills,
        interf.quest_tab to component.gameframe_target_quests,
        interf.inventory_tab to component.gameframe_target_inv,
        interf.equipment_tab to component.gameframe_target_equipment,
        interf.prayer_tab to component.gameframe_target_prayer,
        interf.minimap to component.gameframe_target_minimap,
        interf.spellbook_tab to component.gameframe_target_spells,
        interf.social_tab to component.gameframe_target_social,
        interf.management_tab to component.gameframe_target_management,
        interf.logout_tab to component.gameframe_target_logout,
        interf.settings_tab to component.gameframe_target_settings,
        interf.emotes_tab to component.gameframe_target_emotes,
        interf.music_tab to component.gameframe_target_music,
        interf.clan_tab to component.gameframe_target_clan,
        interf.attack_tab to component.gameframe_target_attack
    )
}
