@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias components = BaseComponents

object BaseComponents : ComponentReferences() {
    val fade_overlay_message = find("fade_overlay:message", 9045250929562636923)

    val hp_hud_container = find("hp_hud:container", 2519922490370555174)
    val hp_hud_hp = find("hp_hud:hp", 7938046680512023329)

    val popout = find("resizable_pane:popout", 4430295738042177582)
    val skills_tab_target = find("resizable_pane:side1", 3182435415241880446)
    val emote_tab_target = find("resizable_pane:side12", 3182435415241880457)
    val orbs_target = find("resizable_pane:orbs", 7781034257266136367)
    val combat_tab_target = find("resizable_pane:side0", 3182435415241880445)
    val chat_target = find("resizable_pane:chat_container", 7378521493207664357)
    val pvp_icons_target = find("resizable_pane:pvp_icons", 4026873813368753076)
    val xp_drops_target = find("resizable_pane:xp_drops", 8696114807461794963)
    val spellbook_tab_target = find("resizable_pane:side6", 3182435415241880451)
    val private_chat_target = find("resizable_pane:pm_container", 5123051510734276976)
    val equipment_tab_target = find("resizable_pane:side4", 3182435415241880449)
    val chat_header_target = find("resizable_pane:side7", 3182435415241880452)
    val settings_tab_target = find("resizable_pane:side11", 3182435415241880456)
    val journal_header_tab_target = find("resizable_pane:side2", 3182435415241880447)
    val inventory_tab_target = find("resizable_pane:side3", 3182435415241880448)
    val prayer_tab_target = find("resizable_pane:side5", 3182435415241880450)
    val friend_list_tab_target = find("resizable_pane:side9", 3182435415241880454)
    val account_management_tab_target = find("resizable_pane:side8", 3182435415241880453)
    val world_switcher_target = find("resizable_pane:side10", 3182435415241880455)
    val hp_hud_target = find("resizable_pane:hpbar_hud", 4026873813368753075)
    val music_tab_target = find("resizable_pane:side13", 3182435415241880458)
    val floater = find("resizable_pane:floater", 7699969845154582066)
    val overlay_atmosphere = find("resizable_pane:overlay_atmosphere", 4026873813368753074)

    val chat_chatmodal = find("chat:chatmodal", 1527608226813778100)

    val player_dialogue_head = find("player_dialogue:head", 5373095475151190962)
    val player_dialogue_name = find("player_dialogue:name", 6182330832416658165)
    val player_dialogue_pbutton = find("player_dialogue:continue", 4040628899889521550)
    val player_dialogue_text = find("player_dialogue:text", 5149519380922689498)

    val npc_dialogue_head = find("npc_dialogue:head", 54411568413849978)
    val npc_dialogue_name = find("npc_dialogue:name", 5384139009496256896)
    val npc_dialogue_pbutton = find("npc_dialogue:continue", 915129872337265177)
    val npc_dialogue_text = find("npc_dialogue:text", 474237396226480908)

    val options_dialogue_pbutton = find("options_dialogue:options", 2354058583546775614)

    val text_dialogue_text = find("text_dialogue:text", 428946190838703861)
    val text_dialogue_pbutton = find("text_dialogue:continue", 2586163870891555941)

    val obj_dialogue_pbutton = find("item_dialogue:universe", 5529957094503964265)
    val obj_dialogue_text = find("item_dialogue:text", 2524578908064496713)
    val obj_dialogue_item = find("item_dialogue:item", 5616504651123485620)

    val double_obj_dialogue_pbutton = find("double_item_dialogue:pausebutton", 330431687310101772)
    val double_obj_dialogue_text = find("double_item_dialogue:text", 3231269493162163273)
    val double_obj_dialogue_model1 = find("double_item_dialogue:model1", 3687424033816124624)
    val double_obj_dialogue_model2 = find("double_item_dialogue:model2", 4738683382078767074)

    val destroy_obj_dialogue_pbutton = find("destroy_item_dialogue:universe", 634370088850376912)

    val menu_list = find("options_menu_dialogue:lj_layer1", 5050712558646226874)

    val mainmodal = find("resizable_pane:mainmodal", 5905850806851984360)
    val sidemodal = find("resizable_pane:sidemodal", 8719636644635355055)

    val inv_items = find("inventory_tab:items", 2716382361977651445)

    val combat_tab_title = find("combat_tab:title", 8061099330540068392)
    val combat_tab_category = find("combat_tab:category", 311653829278247768)
}
