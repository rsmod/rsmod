package org.rsmod.content.interfaces.equipment.configs

import org.rsmod.api.type.refs.obj.ObjReferences

internal typealias equip_objs = EquipmentTabObjs

object EquipmentTabObjs : ObjReferences() {
    val keep_downgraded_without_orn_kit = find("hundred_pirate_mudskipper_hide")
    val keep_downgraded = find("burntfish2")
    val deleted = find("burntfish1")
    val keep = find("burntfish1")
    val gravestone_downgraded = find("burntfish5")
    val gravestone = find("burntfish4")
    val turn_to_coins = find("hundred_pirate_burned_fishcake")
    val lost_to_killer = find("jug_bad_wine")
}
