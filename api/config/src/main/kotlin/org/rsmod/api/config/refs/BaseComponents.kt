package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.comp.ComponentReferences
import org.rsmod.game.type.comp.ComponentType

public typealias components = BaseComponents

public object BaseComponents : ComponentReferences() {
    public val steam_side_panel_target: ComponentType = find(4430295738042177582)
    public val skills_tab_target: ComponentType = find(3182435415241880446)
    public val emote_tab_target: ComponentType = find(3182435415241880457)
    public val orbs_target: ComponentType = find(7781034257266136367)
    public val combat_tab_target: ComponentType = find(3182435415241880445)
    public val chat_target: ComponentType = find(7378521493207664357)
    public val wilderness_overlay_target: ComponentType = find(4026873813368753076)
    public val experience_drops_window_target: ComponentType = find(8696114807461794963)
    public val spellbook_tab_target: ComponentType = find(3182435415241880451)
    public val private_chat_target: ComponentType = find(5123051510734276976)
    public val equipment_tab_target: ComponentType = find(3182435415241880449)
    public val chat_header_target: ComponentType = find(3182435415241880452)
    public val settings_tab_target: ComponentType = find(3182435415241880456)
    public val journal_header_tab_target: ComponentType = find(3182435415241880447)
    public val inventory_tab_target: ComponentType = find(3182435415241880448)
    public val prayer_tab_target: ComponentType = find(3182435415241880450)
    public val friend_list_tab_target: ComponentType = find(3182435415241880454)
    public val account_management_tab_target: ComponentType = find(3182435415241880453)
    public val world_switcher_target: ComponentType = find(3182435415241880455)
    public val hp_hud_target: ComponentType = find(4026873813368753075)
    public val music_tab_target: ComponentType = find(3182435415241880458)

    public val chat_dialogue_target: ComponentType = find(137902143560901726)

    public val player_dialogue_head: ComponentType = find(5373095475151190962)
    public val player_dialogue_title: ComponentType = find(6182330832416658165)
    public val player_dialogue_pbutton: ComponentType = find(4040628899889521550)
    public val player_dialogue_text: ComponentType = find(5149519380922689498)

    public val npc_dialogue_head: ComponentType = find(54411568413849978)
    public val npc_dialogue_title: ComponentType = find(5384139009496256896)
    public val npc_dialogue_pbutton: ComponentType = find(915129872337265177)
    public val npc_dialogue_text: ComponentType = find(474237396226480908)

    public val options_dialogue_pbutton: ComponentType = find(2354058583546775614)

    public val text_dialogue_text: ComponentType = find(428946190838703861)
    public val text_dialogue_pbutton: ComponentType = find(2586163870891555941)

    public val main_modal: ComponentType = find(5905850806851984360)
    public val side_modal: ComponentType = find(8719636644635355055)

    public val shop_subtext: ComponentType = find(1009675651464801228)
    public val shop_side_inv: ComponentType = find(5117171527864918016)
    public val shop_inv: ComponentType = find(7875443253800243706)
}
