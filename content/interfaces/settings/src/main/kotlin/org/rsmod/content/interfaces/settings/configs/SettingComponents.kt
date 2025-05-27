package org.rsmod.content.interfaces.settings.configs

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias setting_components = SettingComponents

object SettingComponents : ComponentReferences() {
    val runbutton_orb = find("orbs:runbutton")
    val runmode = find("settings_side:runmode")

    val settings_tab = find("settings_side:settings_tab")
    val audio_tab = find("settings_side:audio_tab")
    val display_tab = find("settings_side:display_tab")
    val settings_open = find("settings_side:settings_open")

    val skull_prevention = find("settings_side:skull_prevention")
    val attack_priority_player_buttons = find("settings_side:attack_priority_player_buttons")
    val attack_priority_npc_buttons = find("settings_side:attack_priority_npc_buttons")
    val acceptaid = find("settings_side:acceptaid")
    val houseoptions = find("settings_side:houseoptions")
    val bondoptions = find("settings_side:bondoptions")

    val master_icon = find("settings_side:master_icon")
    val master_bobble_container = find("settings_side:master_bobble_container")
    val music_icon = find("settings_side:music_icon")
    val music_bobble_container = find("settings_side:music_bobble_container")
    val sound_icon = find("settings_side:sound_icon")
    val sound_bobble_container = find("settings_side:sound_bobble_container")
    val areasound_icon = find("settings_side:areasound_icon")
    val areasounds_bobble_container = find("settings_side:areasounds_bobble_container")
    val music_toggle = find("settings_side:music_toggle")

    val brightness_bobble_container = find("settings_side:brightness_bobble_container")
    val zoom_toggle = find("settings_side:zoom_toggle")
    val client_type_buttons = find("settings_side:client_type_buttons")
}
