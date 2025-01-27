package org.rsmod.content.interfaces.prayer.tab.configs

import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.obj.ObjType

internal typealias prayer_enums = PrayerTabEnums

object PrayerTabEnums : EnumReferences() {
    val obj_configs = find<Int, ObjType>("prayer_oc", 18447792)
    val attack_collisions = find<Int, Boolean>("prayer_attack_collisions", 3228840)
    val strength_collisions = find<Int, Boolean>("prayer_strength_collisions", 3232561)
    val defence_collisions = find<Int, Boolean>("prayer_defence_collisions", 3236282)
    val overhead_collisions = find<Int, Boolean>("prayer_overhead_collisions", 3240003)
}
