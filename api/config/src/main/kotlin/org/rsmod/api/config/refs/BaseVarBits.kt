@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.varbit.VarBitReferences

typealias varbits = BaseVarBits

object BaseVarBits : VarBitReferences() {
    val chatbox_unlocked = find("chatbox_unlocked", 394391358)
    val modal_widthandheight_mode = find("modal_widthandheight_mode", 231792309)
    val hide_roofs = find("hide_roofs", 697869214)
    val rt7_enabled = find("rt7_enabled", 861505757)
    val rt7_mode = find("rt7_mode", 861509540)
    val rt7_enabled2 = find("rt7_enabled2", 861513323)
    val drop_item_warning = find("drop_item_warning", 64468015)
    val drop_item_minimum_value = find("drop_item_minimum_value", 64473506)
    val combat_tab_weapon_style_type = find("combat_tab_weapon_style_type", 191345645)

    val demon_slayer_progress = find("demon_slayer_progress", 50392587)
}
