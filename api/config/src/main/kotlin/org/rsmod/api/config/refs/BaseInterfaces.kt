package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.interf.InterfaceReferences
import org.rsmod.game.type.interf.InterfaceType

public typealias interfaces = BaseInterfaces

public object BaseInterfaces : InterfaceReferences() {
    public val fixed_pane: InterfaceType = find("fixed_pane", 9223372034726091525)
    public val resizable_pane: InterfaceType = find("resizable_pane", 9223372036366731764)
    public val side_panels_resizable_pane: InterfaceType =
        find("side_panels_resizable_pane", 1080365065)

    public val steam_side_panel: InterfaceType = find("steam_side_panel", 9223372036050024336)
    public val chat: InterfaceType = find("chat", 1912455023)
    public val private_chat: InterfaceType = find("private_chat", 9223372036709596550)
    public val orbs: InterfaceType = find("orbs", 9223372034748519799)
    public val experience_drops_window: InterfaceType =
        find("experience_drops_window", 9223372035590703519)
    public val skills_tab: InterfaceType = find("skills_tab", 9223372036078334889)
    public val journal_header_tab: InterfaceType = find("journal_header_tab", 338347850)
    public val quest_tab: InterfaceType = find("quest_tab", 9223372034739161249)
    public val inventory_tab: InterfaceType = find("inventory_tab", 9223372035777930389)
    public val equipment_tab: InterfaceType = find("equipment_tab", 9223372035270475546)
    public val chat_header: InterfaceType = find("chat_header", 351697622)
    public val settings_tab: InterfaceType = find("settings_tab", 9223372036348650444)
    public val prayer_tab: InterfaceType = find("prayer_tab", 9223372034905333594)
    public val spellbook_tab: InterfaceType = find("spellbook_tab", 643013118)
    public val friend_list_tab: InterfaceType = find("friend_list_tab", 149140580)
    public val account_management_tab: InterfaceType =
        find("account_management_tab", 9223372036107886058)
    public val logout_tab: InterfaceType = find("logout_tab", 2003439444)
    public val emote_tab: InterfaceType = find("emote_tab", 1866350332)
    public val music_tab: InterfaceType = find("music_tab", 9223372035640978172)
    public val chat_channel_tab: InterfaceType = find("chat_channel_tab", 555490233)
    public val world_switcher: InterfaceType = find("world_switcher", 770239458)
    public val combat_tab: InterfaceType = find("combat_tab", 9223372036611818538)
    public val hp_hud: InterfaceType = find("hp_hud", 9223372036744970435)

    public val player_dialogue: InterfaceType = find("player_dialogue", 1128178504)
    public val npc_dialogue: InterfaceType = find("npc_dialogue", 430345552)
    public val options_dialogue: InterfaceType = find("options_dialogue", 151753214)
    public val text_dialogue: InterfaceType = find("text_dialogue", 9223372035695642409)
    public val obj_dialogue: InterfaceType = find("item_dialogue", 1639203600)
    public val double_obj_dialogue: InterfaceType =
        find("double_item_dialogue", 9223372035376440778)
}
