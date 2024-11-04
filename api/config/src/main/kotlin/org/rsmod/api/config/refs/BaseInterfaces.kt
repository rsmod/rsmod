package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.interf.InterfaceReferences
import org.rsmod.game.type.interf.InterfaceType

public typealias interfaces = BaseInterfaces

public object BaseInterfaces : InterfaceReferences() {
    public val fixed_pane: InterfaceType = find(9223372034726091525)
    public val resizable_pane: InterfaceType = find(9223372036366731764)
    public val side_panels_resizable_pane: InterfaceType = find(1080365065)

    public val steam_side_panel: InterfaceType = find(703173897)
    public val chat: InterfaceType = find(1912455023)
    public val private_chat: InterfaceType = find(9223372036709596550)
    public val orbs: InterfaceType = find(9223372034748519799)
    public val experience_drops_window: InterfaceType = find(9223372035590703519)
    public val skills_tab: InterfaceType = find(9223372036078334889)
    public val journal_header_tab: InterfaceType = find(338347850)
    public val quest_tab: InterfaceType = find(9223372034739161249)
    public val inventory_tab: InterfaceType = find(9223372035777930389)
    public val equipment_tab: InterfaceType = find(9223372035270475546)
    public val chat_header: InterfaceType = find(351697622)
    public val settings_tab: InterfaceType = find(9223372036348650444)
    public val prayer_tab: InterfaceType = find(9223372034905333594)
    public val spellbook_tab: InterfaceType = find(1423550780)
    public val friend_list_tab: InterfaceType = find(149140580)
    public val account_management_tab: InterfaceType = find(9223372036107886058)
    public val logout_tab: InterfaceType = find(9223372036754073083)
    public val emote_tab: InterfaceType = find(1866350332)
    public val music_tab: InterfaceType = find(9223372035640978172)
    public val chat_channel_tab: InterfaceType = find(555490233)
    public val world_switcher: InterfaceType = find(770239458)
    public val combat_tab: InterfaceType = find(9223372036327241509)
    public val hp_hud: InterfaceType = find(9223372036744970435)

    public val player_dialogue: InterfaceType = find(1128178504)
    public val npc_dialogue: InterfaceType = find(430345552)
    public val options_dialogue: InterfaceType = find(151753214)
    public val text_dialogue: InterfaceType = find(9223372035695642409)
    public val obj_dialogue: InterfaceType = find(1639203600)
    public val double_obj_dialogue: InterfaceType = find(9223372035376440778)
}
