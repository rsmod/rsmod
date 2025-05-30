@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.npc.NpcReferences

typealias npcs = BaseNpcs

object BaseNpcs : NpcReferences() {
    val man = find("man", 1329978397240899572)
    val man2 = find("man2", 1329978397240899573)
    val man3 = find("man3", 1329978397240899574)
    val woman = find("woman", 5318832715787091329)
    val woman2 = find("woman2", 5318832715787091330)
    val woman3 = find("woman3", 5318832715787091331)
    val man_indoor = find("man_indoor", 1329978397240903284)
    val uri_emote_1 = find("trail_master_uri", 2255033183848053726)
    val uri_emote_2 = find("uri_emote", 4998005704130117537)
    val diary_emote_npc = find("diary_emote_npc", 8481684566064664364)
    val corp_beast = find("corp_beast", 5604977903323694725)
    val imp = find("imp", 61762237712635356)
    val farming_tools_leprechaun = find("farming_tools_leprechaun", 5428755996588687321)
    val rod_fishing_spot_1527 = find("0_50_50_freshfish", 1358863933022409758)
    val fishing_spot_1530 = find("0_50_49_saltfish", 8943009170502558049)

    val spirit_tree_chathead = find("pog_spirit_tree_healthy_dummy")
}
