@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias components = BaseComponents

object BaseComponents : ComponentReferences() {
    val mainmodal = find("toplevel_osrs_stretch:mainmodal", 5905850806851984360)
    val sidemodal = find("toplevel_osrs_stretch:sidemodal", 8719636644635355055)

    val hp_hud_container = find("hpbar_hud:container", 2519922490370555174)
    val hp_hud_hp = find("hpbar_hud:hp", 7938046680512023329)

    val toplevel_target_ehc_listener =
        find("toplevel_osrs_stretch:ehc_listener", 7892556120849742062)
    val toplevel_target_buff_bar = find("toplevel_osrs_stretch:buff_bar", 6917295404844201161)
    val toplevel_target_stat_boosts_hud =
        find("toplevel_osrs_stretch:stat_boosts_hud", 6917295404844201160)
    val toplevel_target_popout = find("toplevel_osrs_stretch:popout", 4430295738042177582)
    val toplevel_target_side0 = find("toplevel_osrs_stretch:side0", 3182435415241880445)
    val toplevel_target_side1 = find("toplevel_osrs_stretch:side1", 3182435415241880446)
    val toplevel_target_side2 = find("toplevel_osrs_stretch:side2", 3182435415241880447)
    val toplevel_target_side3 = find("toplevel_osrs_stretch:side3", 3182435415241880448)
    val toplevel_target_side4 = find("toplevel_osrs_stretch:side4", 3182435415241880449)
    val toplevel_target_side5 = find("toplevel_osrs_stretch:side5", 3182435415241880450)
    val toplevel_target_side6 = find("toplevel_osrs_stretch:side6", 3182435415241880451)
    val toplevel_target_side7 = find("toplevel_osrs_stretch:side7", 3182435415241880452)
    val toplevel_target_side8 = find("toplevel_osrs_stretch:side8", 3182435415241880453)
    val toplevel_target_side9 = find("toplevel_osrs_stretch:side9", 3182435415241880454)
    val toplevel_target_side10 = find("toplevel_osrs_stretch:side10", 3182435415241880455)
    val toplevel_target_side11 = find("toplevel_osrs_stretch:side11", 3182435415241880456)
    val toplevel_target_side12 = find("toplevel_osrs_stretch:side12", 3182435415241880457)
    val toplevel_target_side13 = find("toplevel_osrs_stretch:side13", 3182435415241880458)
    val toplevel_target_orbs = find("toplevel_osrs_stretch:orbs", 7781034257266136367)
    val toplevel_target_chat_container =
        find("toplevel_osrs_stretch:chat_container", 7378521493207664357)
    val toplevel_target_pvp_icons = find("toplevel_osrs_stretch:pvp_icons", 4026873813368753076)
    val toplevel_target_xp_drops = find("toplevel_osrs_stretch:xp_drops", 8696114807461794963)
    val toplevel_target_pm_container =
        find("toplevel_osrs_stretch:pm_container", 5123051510734276976)
    val toplevel_target_hpbar_hud = find("toplevel_osrs_stretch:hpbar_hud", 4026873813368753075)
    val toplevel_target_floater = find("toplevel_osrs_stretch:floater", 7699969845154582066)
    val toplevel_target_overlay_atmosphere =
        find("toplevel_osrs_stretch:overlay_atmosphere", 4026873813368753074)
    val toplevel_target_maincrm = find("toplevel_osrs_stretch:maincrm", 5905850806851984361)
    val toplevel_target_sidecrm = find("toplevel_osrs_stretch:sidecrm", 6735973694115455439)
    val toplevel_target_overlay_hud = find("toplevel_osrs_stretch:overlay_hud", 5805693215986346880)
    val toplevel_target_zeah = find("toplevel_osrs_stretch:zeah", 8696114807461794964)
    val toplevel_target_helper_content =
        find("toplevel_osrs_stretch:helper_content", 125361679448962740)

    val chatbox_chatmodal = find("chatbox:chatmodal", 1527608226813778100)

    val chat_right_head = find("chat_right:head", 5373095475151190962)
    val chat_right_name = find("chat_right:name", 6182330832416658165)
    val chat_right_pbutton = find("chat_right:continue", 4040628899889521550)
    val chat_right_text = find("chat_right:text", 5149519380922689498)

    val chat_left_head = find("chat_left:head", 54411568413849978)
    val chat_left_name = find("chat_left:name", 5384139009496256896)
    val chat_left_pbutton = find("chat_left:continue", 915129872337265177)
    val chat_left_text = find("chat_left:text", 474237396226480908)

    val chatmenu_pbutton = find("chatmenu:options", 2354058583546775614)

    val messagebox_text = find("messagebox:text", 4817549332312421939)
    val messagebox_pbutton = find("messagebox:continue", 1367557556890583596)

    val objectbox_pbutton = find("objectbox:universe", 5529957094503964265)
    val objectbox_text = find("objectbox:text", 2524578908064496713)
    val objectbox_item = find("objectbox:item", 5616504651123485620)

    val objectbox_double_pbutton = find("objectbox_double:pausebutton", 330431687310101772)
    val objectbox_double_text = find("objectbox_double:text", 3231269493162163273)
    val objectbox_double_model1 = find("objectbox_double:model1", 3687424033816124624)
    val objectbox_doublee_model2 = find("objectbox_double:model2", 4738683382078767074)

    val confirmdestroy_pbutton = find("confirmdestroy:universe", 634370088850376912)

    val menu_list = find("menu:lj_layer1", 5050712558646226874)

    val inv_items = find("inventory:items", 2716382361977651445)

    val combat_tab_title = find("combat_interface:title", 7028397761873724972)
    val combat_tab_category = find("combat_interface:category", 311653829278247768)

    val fade_overlay_message = find("fade_overlay:message", 9045250929562636923)

    val music_now_playing_text = find("music:now_playing_text", 7229867128976358672)
}
