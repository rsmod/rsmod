package org.rsmod.content.interfaces.equipment.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias equip_objs = EquipmentTabObjs

object EquipmentTabObjs : ObjReferences() {
    val keep_downgraded_without_orn_kit = find("mudskipper_hide")
    val keep_downgraded = find("burnt_fish_trout")
    val deleted = find("burnt_fish_anchovies")
    val keep = find("burnt_fish_anchovies")
    val gravestone_downgraded = find("burnt_fish_sardine")
    val gravestone = find("burnt_fish_tuna")
    val turn_to_coins = find("burnt_fishcake")
    val lost_to_killer = find("jug_of_bad_wine")
}
