package org.rsmod.api.config.refs

import org.rsmod.api.config.aliases.EnumComp
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.enums.EnumType

public typealias enums = BaseEnums

public object BaseEnums : EnumReferences() {
    public val equipment_tab_to_slots_map: EnumType<Int, EnumComp> =
        find("equipment_tab_to_slots_map")
}
