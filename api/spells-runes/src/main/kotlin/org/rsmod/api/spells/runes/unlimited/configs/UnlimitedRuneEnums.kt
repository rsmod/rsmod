package org.rsmod.api.spells.runes.unlimited.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.obj.ObjType

internal typealias unlimited_enums = UnlimitedRuneEnums

internal object UnlimitedRuneEnums : EnumReferences() {
    val rune_staves: EnumType<ObjType, EnumType<ObjType, Boolean>> = find("rune_staves")
    val air_staves: EnumType<ObjType, Boolean> = find("air_rune_staves")
    val water_staves: EnumType<ObjType, Boolean> = find("water_rune_staves")
    val earth_staves: EnumType<ObjType, Boolean> = find("earth_rune_staves")
    val fire_staves: EnumType<ObjType, Boolean> = find("fire_rune_staves")

    val high_priority: EnumType<ObjType, EnumType<Int, ObjType>> = find("unlimited_runes_hiprio")
    val air_high_priority: EnumType<Int, ObjType> = find("air_unlimited_runes_hiprio")
    val water_high_priority: EnumType<Int, ObjType> = find("water_unlimited_runes_hiprio")
    val earth_high_priority: EnumType<Int, ObjType> = find("earth_unlimited_runes_hiprio")
    val fire_high_priority: EnumType<Int, ObjType> = find("fire_unlimited_runes_hiprio")

    val low_priority: EnumType<ObjType, EnumType<Int, ObjType>> = find("unlimited_runes_loprio")
    val water_low_priority: EnumType<Int, ObjType> = find("water_unlimited_runes_loprio")
    val earth_low_priority: EnumType<Int, ObjType> = find("earth_unlimited_runes_loprio")
    val fire_low_priority: EnumType<Int, ObjType> = find("fire_unlimited_runes_loprio")
    val nature_low_priority: EnumType<Int, ObjType> = find("nature_unlimited_runes_loprio")
}

internal object UnlimitedRuneEnumBuilder : EnumBuilder() {
    init {
        build<ObjType, EnumType<ObjType, Boolean>>("rune_staves") {
            this[objs.air_rune] = unlimited_enums.air_staves
            this[objs.water_rune] = unlimited_enums.water_staves
            this[objs.earth_rune] = unlimited_enums.earth_staves
            this[objs.fire_rune] = unlimited_enums.fire_staves
        }

        build<ObjType, EnumType<Int, ObjType>>("unlimited_runes_hiprio") {
            this[objs.air_rune] = unlimited_enums.air_high_priority
            this[objs.water_rune] = unlimited_enums.water_high_priority
            this[objs.earth_rune] = unlimited_enums.earth_high_priority
            this[objs.fire_rune] = unlimited_enums.fire_high_priority
        }

        buildAutoInt<ObjType>("air_unlimited_runes_hiprio") { this += objs.devils_element }
        buildAutoInt<ObjType>("water_unlimited_runes_hiprio") { this += objs.devils_element }
        buildAutoInt<ObjType>("earth_unlimited_runes_hiprio") { this += objs.devils_element }
        buildAutoInt<ObjType>("fire_unlimited_runes_hiprio") { this += objs.devils_element }

        build<ObjType, EnumType<Int, ObjType>>("unlimited_runes_loprio") {
            this[objs.water_rune] = unlimited_enums.water_low_priority
            this[objs.earth_rune] = unlimited_enums.earth_low_priority
            this[objs.fire_rune] = unlimited_enums.fire_low_priority
            this[objs.nature_rune] = unlimited_enums.nature_low_priority
        }

        buildAutoInt<ObjType>("water_unlimited_runes_loprio") { this += objs.tome_of_water }
        buildAutoInt<ObjType>("earth_unlimited_runes_loprio") { this += objs.tome_of_earth }
        buildAutoInt<ObjType>("fire_unlimited_runes_loprio") { this += objs.tome_of_fire }
        buildAutoInt<ObjType>("nature_unlimited_runes_loprio") { this += objs.bryophytas_staff }
    }
}
