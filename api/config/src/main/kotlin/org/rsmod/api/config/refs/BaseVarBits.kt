@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias varbits = BaseVarBits

object BaseVarBits : VarBitReferences() {
    val chatbox_unlocked = find("chatbox_unlocked", 125361)
    val modal_widthandheight_mode = find("modal_widthandheight_mode", 44708)
    val hide_roofs = find("hide_roofs", 129620)
    val rt7_enabled = find("rt7_enabled", 112862)
    val rt7_mode = find("rt7_mode", 116645)
    val rt7_enabled2 = find("rt7_enabled2", 120428)
    val drop_item_warning = find("drop_item_warning", 5411)
    val drop_item_minimum_value = find("drop_item_minimum_value", 10902)
    val combat_tab_weapon_style_type = find("combat_tab_weapon_style_type", 662)
    val esc_closes_current_interface = find("esc_closes_current_interface")

    val demon_slayer_progress = find("demon_slayer_progress", 2805)
    val lost_tribe_progress = find("lost_tribe_quest")
    val glass_box_emote = find("glass_box_emote")
    val climb_rope_emote = find("climb_rope_emote")
    val lean_emote = find("lean_emote")
    val glass_wall_emote = find("glass_wall_emote")
    val idea_emote = find("idea_emote")
    val stamp_emote = find("stamp_emote")
    val flap_emote = find("flap_emote")
    val slap_head_emote = find("slap_head_emote")
    val zombie_walk_emote = find("zombie_walk_emote")
    val zombie_dance_emote = find("zombie_dance_emote")
    val scared_emote = find("scared_emote")
    val rabbit_hop_emote = find("rabbit_hop_emote")
    val drill_demon_emotes = find("drill_demon_emotes")
    val party_emote = find("party_emote")
    val zombie_hand_emote = find("zombie_hand_emote")
    val hypermobile_drinker_emote = find("hypermobile_drinker_emote")
    val air_guitar_emote = find("air_guitar_emote")
    val uri_transform_emote = find("uri_transform_emote")
    val smooth_dance_emote = find("smooth_dance_emote")
    val premier_shield_emote = find("premier_shield_emote")
    val flex_emote = find("flex_emote")
    val explore_emote = find("explore_emote")
    val relic_unlock_emote = find("relic_unlock_emote")
    val trick_emote = find("trick_emote")
}
