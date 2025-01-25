package org.rsmod.content.interfaces.equipment.configs

import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.comp.ComponentType

typealias equip_enums = EquipmentTabEnums

object EquipmentTabEnums : EnumReferences() {
    val mapped_wearpos = find<Int, ComponentType>("equipment_stats_to_slots_map", 10335974)
}
