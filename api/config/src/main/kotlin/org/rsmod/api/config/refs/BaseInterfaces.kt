@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.interf.InterfaceReferences

typealias interfaces = BaseInterfaces

object BaseInterfaces : InterfaceReferences() {
    val fade_overlay = find("fade_overlay", 9223372035931967580)

    val bank_main = find("bankmain", 479914382)
    val bank_side = find("bankside", 1999686874)
    val bankpin_settings = find("bankpin_settings", 642937557)

    val toplevel = find("toplevel", 9223372034726091525)
    val toplevel_osrs_stretch = find("toplevel_osrs_stretch", 9223372035990794041)
    val toplevel_pre_eoc = find("toplevel_pre_eoc", 9223372034871006052)

    val buff_bar = find("buff_bar", 9223372034871751765)
    val stat_boosts_hud = find("stat_boosts_hud", 1593617132)
    val pvp_icons = find("pvp_icons", 947251801)
    val ehc_worldhop = find("ehc_worldhop", 9223372036687970629)
    val chatbox = find("chatbox", 9223372035380135649)
    val popout = find("popout", 9223372036050024336)
    val pm_chat = find("pm_chat", 9223372036709596550)
    val orbs = find("orbs", 9223372034748519799)
    val xp_drops = find("xp_drops", 9223372035590703519)
    val stats = find("stats", 9223372036078334889)
    val side_journal = find("side_journal", 9223372034964910410)
    val questlist = find("questlist", 9223372034739161249)
    val inventory = find("inventory", 9223372035777930389)
    val wornitems = find("wornitems", 9223372035270475546)
    val side_channels = find("side_channels", 1832628328)
    val settings_side = find("settings_side", 657569579)
    val prayerbook = find("prayerbook", 9223372034905333594)
    val magic_spellbook = find("magic_spellbook", 2027746394)
    val friends = find("friends", 149140580)
    val account = find("account", 9223372036045351858)
    val logout = find("logout", 2003439444)
    val emote = find("emote", 1866350332)
    val music = find("music", 9223372035640978172)
    val chatchannel_current = find("chatchannel_current", 555490233)
    val worldswitcher = find("worldswitcher", 770239458)
    val combat_interface = find("combat_interface", 1846453943)
    val hpbar_hud = find("hpbar_hud", 9223372036744970435)

    val account_summary_sidepanel = find("account_summary_sidepanel", 1142196774)
    val area_task = find("area_task", 1628778927)

    val chat_right = find("chat_right", 1128178504)
    val chat_left = find("chat_left", 430345552)
    val chatmenu = find("chatmenu", 151753214)
    val messagebox = find("messagebox", 1781919053)
    val obj_dialogue = find("objectbox", 1639203600)
    val double_obj_dialogue = find("objectbox_double", 9223372035376440778)
    val destroy_obj_dialogue = find("confirmdestroy", 923450919)
    val menu = find("menu", 130230041)

    val popupoverlay = find("popupoverlay", 9223372035707796330)
    val ge_collection_box = find("ge_collect", 9223372036801773648)
    val ca_overview = find("ca_overview", 9223372035062537910)
    val collection = find("collection", 1925563159)
    val bond_main = find("bond_main", 9223372036023293853)
    val poh_options = find("poh_options", 9223372035476859568)
    val settings = find("settings", 1605189305)
}
