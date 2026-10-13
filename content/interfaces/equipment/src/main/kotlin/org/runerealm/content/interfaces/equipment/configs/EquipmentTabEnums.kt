package org.runerealm.content.interfaces.equipment.configs

import org.runerealm.api.type.refs.enums.EnumReferences
import org.runerealm.game.type.comp.ComponentType

typealias equip_enums = EquipmentTabEnums

object EquipmentTabEnums : EnumReferences() {
    val mapped_wearpos = find<Int, ComponentType>("equipment_stats_to_slots_map", 10335974)
}
