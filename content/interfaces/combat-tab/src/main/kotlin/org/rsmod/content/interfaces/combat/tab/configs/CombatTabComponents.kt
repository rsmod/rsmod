package org.rsmod.content.interfaces.combat.tab.configs

import org.rsmod.api.type.refs.comp.ComponentReferences

typealias combat_components = CombatTabComponents

object CombatTabComponents : ComponentReferences() {
    val style0 = find("combat_tab_com5", 2858390886771847763)
    val style1 = find("combat_tab_com9", 2437616626352755401)
    val style2 = find("combat_tab_com13", 4786378263585440964)
    val style3 = find("combat_tab_com17", 4365604003166348602)
    val auto_retaliate = find("combat_tab_com31", 6232502627085836658)
}
