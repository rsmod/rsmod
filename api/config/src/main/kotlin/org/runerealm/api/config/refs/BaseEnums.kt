@file:Suppress("SpellCheckingInspection", "unused")

package org.runerealm.api.config.refs

import org.runerealm.api.config.aliases.EnumComp
import org.runerealm.api.type.refs.enums.EnumReferences
import org.runerealm.game.type.enums.EnumType

typealias enums = BaseEnums

object BaseEnums : EnumReferences() {
    val equipment_tab_to_slots_map: EnumType<Int, EnumComp> = find("equipment_tab_to_slots_map")
}
