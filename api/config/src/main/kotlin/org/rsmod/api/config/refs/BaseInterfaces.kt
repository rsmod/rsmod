package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.interf.InterfaceReferences
import org.rsmod.game.type.interf.InterfaceType

public typealias interfaces = BaseInterfaces

public object BaseInterfaces : InterfaceReferences() {
    public val fixed_pane: InterfaceType = find(9223372034774454679)
    public val resizable_pane: InterfaceType = find(1504833148)
    public val side_panels_resizable_pane: InterfaceType = find(1238022487)

    public val steam_side_panel: InterfaceType = find(9223372035027709583)
    public val chat: InterfaceType = find(9223372035920617671)
    public val private_chat: InterfaceType = find(9223372036709596550)
    public val orbs: InterfaceType = find(2019863765)
    public val experience_drops_window: InterfaceType = find(9223372035670929907)
    public val skills_tab: InterfaceType = find(9223372034955071141)
    public val journal_header_tab: InterfaceType = find(2011552674)
    public val quest_tab: InterfaceType = find(9223372034739161249)
    public val inventory_tab: InterfaceType = find(9223372035777930389)
    public val equipment_tab: InterfaceType = find(9223372036275288224)
    public val chat_header: InterfaceType = find(1679783284)
    public val settings_tab: InterfaceType = find(9223372036602181164)
    public val prayer_tab: InterfaceType = find(1689788066)
    public val spellbook_tab: InterfaceType = find(1619268208)
    public val friend_list_tab: InterfaceType = find(9223372036581225610)
    public val account_management_tab: InterfaceType = find(9223372036671469950)
    public val logout_tab: InterfaceType = find(675428985)
    public val emote_tab: InterfaceType = find(9223372034962100068)
    public val music_tab: InterfaceType = find(9223372035753214276)
    public val chat_channel_tab: InterfaceType = find(1389586693)
    public val world_switcher: InterfaceType = find(9223372035955119046)
    public val combat_tab: InterfaceType = find(9223372035396249889)
    public val hp_hud: InterfaceType = find(9223372035771200973)

    public val player_dialogue: InterfaceType = find(9223372035583234704)
    public val npc_dialogue: InterfaceType = find(467920994)
    public val options_dialogue: InterfaceType = find(151753214)
    public val text_dialogue: InterfaceType = find(9223372035695642409)
    public val obj_dialogue: InterfaceType = find(1639203600)
    public val double_obj_dialogue: InterfaceType = find(9223372036531694986)
}
