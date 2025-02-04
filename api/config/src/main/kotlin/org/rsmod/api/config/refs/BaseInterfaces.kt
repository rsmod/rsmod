@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.interf.InterfaceReferences

typealias interfaces = BaseInterfaces

object BaseInterfaces : InterfaceReferences() {
    val bank_main = find("bank", 261123712)
    val bank_side = find("bank_inventory", 1999686874)

    val fixed_pane = find("fixed_pane", 9223372034726091525)
    val resizable_pane = find("resizable_pane", 9223372036366731764)
    val side_panels_resizable_pane = find("side_panels_resizable_pane", 1080365065)

    val steam_side_panel = find("steam_side_panel", 9223372036050024336)
    val chat = find("chat", 9223372035380135649)
    val private_chat = find("private_chat", 9223372036709596550)
    val orbs = find("orbs", 9223372034748519799)
    val experience_drops_window = find("experience_drops_window", 9223372035590703519)
    val skills_tab = find("skills_tab", 9223372036078334889)
    val journal_header_tab = find("journal_header_tab", 9223372034964910410)
    val quest_tab = find("quest_tab", 9223372034739161249)
    val inventory_tab = find("inventory_tab", 9223372035777930389)
    val equipment_tab = find("equipment_tab", 9223372035270475546)
    val chat_header = find("chat_header", 1832628328)
    val settings_tab = find("settings_tab", 1694635289)
    val prayer_tab = find("prayer_tab", 9223372034905333594)
    val spellbook_tab = find("spellbook_tab", 643013118)
    val friend_list_tab = find("friend_list_tab", 149140580)
    val account_management_tab = find("account_management_tab", 9223372036045351858)
    val logout_tab = find("logout_tab", 2003439444)
    val emote_tab = find("emote_tab", 1866350332)
    val music_tab = find("music_tab", 9223372035640978172)
    val chat_channel_tab = find("chat_channel_tab", 555490233)
    val world_switcher = find("world_switcher", 770239458)
    val combat_tab = find("combat_tab", 9223372036611818538)
    val hp_hud = find("hp_hud", 9223372036744970435)

    val player_dialogue = find("player_dialogue", 1128178504)
    val npc_dialogue = find("npc_dialogue", 430345552)
    val options_dialogue = find("options_dialogue", 151753214)
    val text_dialogue = find("text_dialogue", 9223372035695642409)
    val obj_dialogue = find("item_dialogue", 1639203600)
    val double_obj_dialogue = find("double_item_dialogue", 9223372035376440778)
    val destroy_obj_dialogue = find("destroy_item_dialogue", 923450919)

    val overlay_confirmation = find("overlay_confirmation", 9223372035707796330)
}
