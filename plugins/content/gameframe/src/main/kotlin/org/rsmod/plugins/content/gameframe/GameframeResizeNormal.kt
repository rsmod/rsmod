package org.rsmod.plugins.content.gameframe

import org.rsmod.game.types.NamedComponent
import org.rsmod.game.types.NamedInterface
import org.rsmod.plugins.api.component
import org.rsmod.plugins.api.gameframe_resize_normal
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
import org.rsmod.plugins.api.interf
import org.rsmod.plugins.api.model.ui.StandardGameframe

public object GameframeResizeNormal : StandardGameframe {

    override val topLevel: NamedInterface = interf.gameframe_resize_normal

    override val overlays: List<NamedComponent> = listOf(
        component.gameframe_target_chatbox,
        component.gameframe_target_username,
        component.gameframe_target_pvp,
        component.gameframe_target_hud,
        component.gameframe_target_minimap,
        component.gameframe_target_xp,
        component.gameframe_target_skills,
        component.gameframe_target_quests,
        component.gameframe_target_inv,
        component.gameframe_target_equipment,
        component.gameframe_target_prayer,
        component.gameframe_target_minimap,
        component.gameframe_target_spells,
        component.gameframe_target_social,
        component.gameframe_target_management,
        component.gameframe_target_logout,
        component.gameframe_target_settings,
        component.gameframe_target_emotes,
        component.gameframe_target_music,
        component.gameframe_target_clan,
        component.gameframe_target_attack
    )
}
