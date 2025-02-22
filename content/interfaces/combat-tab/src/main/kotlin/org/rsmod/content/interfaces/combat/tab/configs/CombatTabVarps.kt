package org.rsmod.content.interfaces.combat.tab.configs

import org.rsmod.api.type.builders.varp.VarpBuilder
import org.rsmod.api.type.refs.varp.VarpReferences

typealias combat_varps = CombatTabVarps

object CombatTabVarps : VarpReferences() {
    val saved_attackstyle1 = find("weapons_saved_attackstyle1")
    val saved_attackstyle2 = find("weapons_saved_attackstyle2")
}

object CombatTabVarpBuilder : VarpBuilder() {
    init {
        build("weapons_saved_attackstyle1")
        build("weapons_saved_attackstyle2")
    }
}
