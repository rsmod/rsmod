package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.obj.ObjType

internal typealias prayer_enums = PrayerTabEnums

object PrayerTabEnums : EnumReferences() {
    val obj_configs = find<Int, ObjType>("prayer_oc")
    val attack_collisions = find<Int, Boolean>("prayer_attack_collisions")
    val strength_collisions = find<Int, Boolean>("prayer_strength_collisions")
    val defence_collisions = find<Int, Boolean>("prayer_defence_collisions")
    val overhead_collisions = find<Int, Boolean>("prayer_overhead_collisions")
}
