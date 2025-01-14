package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.game.type.comp.ComponentType

public typealias components = BaseComponents

public object BaseComponents : ComponentReferences() {
    public val steam_side_panel_target: ComponentType =
        find("resizable_pane_com35", 4430295738042177582)
    public val skills_tab_target: ComponentType = find("resizable_pane_com77", 3182435415241880446)
    public val emote_tab_target: ComponentType = find("resizable_pane_com88", 3182435415241880457)
    public val orbs_target: ComponentType = find("resizable_pane_com33", 7781034257266136367)
    public val combat_tab_target: ComponentType = find("resizable_pane_com76", 3182435415241880445)
    public val chat_target: ComponentType = find("resizable_pane_com96", 7378521493207664357)
    public val wilderness_overlay_target: ComponentType =
        find("resizable_pane_com3", 4026873813368753076)
    public val experience_drops_window_target: ComponentType =
        find("resizable_pane_com9", 8696114807461794963)
    public val spellbook_tab_target: ComponentType =
        find("resizable_pane_com82", 3182435415241880451)
    public val private_chat_target: ComponentType =
        find("resizable_pane_com93", 5123051510734276976)
    public val equipment_tab_target: ComponentType =
        find("resizable_pane_com80", 3182435415241880449)
    public val chat_header_target: ComponentType = find("resizable_pane_com83", 3182435415241880452)
    public val settings_tab_target: ComponentType =
        find("resizable_pane_com87", 3182435415241880456)
    public val journal_header_tab_target: ComponentType =
        find("resizable_pane_com78", 3182435415241880447)
    public val inventory_tab_target: ComponentType =
        find("resizable_pane_com79", 3182435415241880448)
    public val prayer_tab_target: ComponentType = find("resizable_pane_com81", 3182435415241880450)
    public val friend_list_tab_target: ComponentType =
        find("resizable_pane_com85", 3182435415241880454)
    public val account_management_tab_target: ComponentType =
        find("resizable_pane_com84", 3182435415241880453)
    public val world_switcher_target: ComponentType =
        find("resizable_pane_com86", 3182435415241880455)
    public val hp_hud_target: ComponentType = find("resizable_pane_com2", 4026873813368753075)
    public val music_tab_target: ComponentType = find("resizable_pane_com89", 3182435415241880458)

    public val chat_dialogue_target: ComponentType = find("chat_com565", 137902143560901726)

    public val player_dialogue_head: ComponentType =
        find("player_dialogue_com2", 5373095475151190962)
    public val player_dialogue_title: ComponentType =
        find("player_dialogue_com4", 6182330832416658165)
    public val player_dialogue_pbutton: ComponentType =
        find("player_dialogue_com5", 4040628899889521550)
    public val player_dialogue_text: ComponentType =
        find("player_dialogue_com6", 5149519380922689498)

    public val npc_dialogue_head: ComponentType = find("npc_dialogue_com2", 54411568413849978)
    public val npc_dialogue_title: ComponentType = find("npc_dialogue_com4", 5384139009496256896)
    public val npc_dialogue_pbutton: ComponentType = find("npc_dialogue_com5", 915129872337265177)
    public val npc_dialogue_text: ComponentType = find("npc_dialogue_com6", 474237396226480908)

    public val options_dialogue_pbutton: ComponentType =
        find("options_dialogue_com1", 2354058583546775614)

    public val text_dialogue_text: ComponentType = find("text_dialogue_com1", 428946190838703861)
    public val text_dialogue_pbutton: ComponentType =
        find("text_dialogue_com2", 2586163870891555941)

    public val obj_dialogue_pbutton: ComponentType = find("item_dialogue_com0", 5529957094503964265)
    public val obj_dialogue_text: ComponentType = find("item_dialogue_com2", 2524578908064496713)
    public val obj_dialogue_obj: ComponentType = find("item_dialogue_com1", 5616504651123485620)

    public val double_obj_dialogue_pbutton: ComponentType =
        find("double_item_dialogue_com4", 330431687310101772)
    public val double_obj_dialogue_text: ComponentType =
        find("double_item_dialogue_com2", 3231269493162163273)
    public val double_obj_dialogue_obj1: ComponentType =
        find("double_item_dialogue_com1", 3687424033816124624)
    public val double_obj_dialogue_obj2: ComponentType =
        find("double_item_dialogue_com3", 4738683382078767074)

    public val destroy_obj_dialogue_pbutton: ComponentType =
        find("destroy_item_dialogue_com0", 634370088850376912)

    public val main_modal: ComponentType = find("resizable_pane_com16", 5905850806851984360)
    public val side_modal: ComponentType = find("resizable_pane_com74", 8719636644635355055)

    public val inv_inv: ComponentType = find("inventory_tab_com0", 2716382361977651445)
}
