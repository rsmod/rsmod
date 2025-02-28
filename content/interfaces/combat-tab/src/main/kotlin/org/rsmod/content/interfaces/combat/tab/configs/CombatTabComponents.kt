package org.rsmod.content.interfaces.combat.tab.configs

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias combat_components = CombatTabComponents

object CombatTabComponents : ComponentReferences() {
    val stance1 = find("combat_tab_com5", 2858390886771847763)
    val stance2 = find("combat_tab_com9", 2437616626352755401)
    val stance3 = find("combat_tab_com13", 4786378263585440964)
    val stance4 = find("combat_tab_com17", 4365604003166348602)
    val auto_retaliate = find("combat_tab_com31", 6232502627085836658)
    val special_attack = find("combat_tab_com38", 5418117387536405546)

    val special_attack_orb = find("orbs_com35", 4900391869574997453)
}
