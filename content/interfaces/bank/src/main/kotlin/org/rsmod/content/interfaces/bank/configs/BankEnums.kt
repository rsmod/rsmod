package org.rsmod.content.interfaces.bank.configs

import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.comp.ComponentType

internal typealias bank_enums = BankEnums

object BankEnums : EnumReferences() {
    val worn_component_map = find<Int, ComponentType>("bank_equipment_tab_to_slots_map")
}
