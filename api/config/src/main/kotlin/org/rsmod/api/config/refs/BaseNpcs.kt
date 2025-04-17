@file:Suppress("SpellCheckingInspection", "unused")

package org.rsmod.api.config.refs

import org.rsmod.api.type.refs.npc.NpcReferences

typealias npcs = BaseNpcs

object BaseNpcs : NpcReferences() {
    val man = find("man", 7341705936856477347)
    val man2 = find("man2", 7341705936856477348)
    val man3 = find("man3", 7341705936856477349)
    val woman = find("woman", 1630774373095227408)
    val woman2 = find("woman2", 1630774373095227409)
    val woman3 = find("woman3", 1630774373095227410)
    val man_indoor = find("man_indoor", 7341705936856481059)
    val uri_emote_1 = find("trail_master_uri", 8429815698763973593)
    val uri_emote_2 = find("uri_emote", 507070735729125592)
    val diary_emote_npc = find("diary_emote_npc", 873924466076745435)
    val corp_beast = find("corp_beast", 638886739118650468)
}
