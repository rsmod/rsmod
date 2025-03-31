package org.rsmod.api.spells.runes.combo.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.obj.ObjType

internal typealias combo_enums = ComboRuneEnums

internal object ComboRuneEnums : EnumReferences() {
    val combos: EnumType<ObjType, EnumType<Int, ObjType>> = find("combo_runes")
    val mist_rune: EnumType<Int, ObjType> = find("combo_rune_mist")
    val dust_rune: EnumType<Int, ObjType> = find("combo_rune_dust")
    val mud_rune: EnumType<Int, ObjType> = find("combo_rune_mud")
    val smoke_rune: EnumType<Int, ObjType> = find("combo_rune_smoke")
    val steam_rune: EnumType<Int, ObjType> = find("combo_rune_steam")
    val lava_rune: EnumType<Int, ObjType> = find("combo_rune_lava")
}

internal object ComboRuneEnumBuilder : EnumBuilder() {
    init {
        build<ObjType, EnumType<Int, ObjType>>("combo_runes") {
            this[objs.mist_rune] = combo_enums.mist_rune
            this[objs.dust_rune] = combo_enums.dust_rune
            this[objs.mud_rune] = combo_enums.mud_rune
            this[objs.smoke_rune] = combo_enums.smoke_rune
            this[objs.steam_rune] = combo_enums.steam_rune
            this[objs.lava_rune] = combo_enums.lava_rune
        }

        buildAutoInt<ObjType>("combo_rune_mist") {
            this += objs.air_rune
            this += objs.water_rune
        }

        buildAutoInt<ObjType>("combo_rune_dust") {
            this += objs.air_rune
            this += objs.earth_rune
        }

        buildAutoInt<ObjType>("combo_rune_mud") {
            this += objs.water_rune
            this += objs.earth_rune
        }

        buildAutoInt<ObjType>("combo_rune_smoke") {
            this += objs.air_rune
            this += objs.fire_rune
        }

        buildAutoInt<ObjType>("combo_rune_steam") {
            this += objs.water_rune
            this += objs.fire_rune
        }

        buildAutoInt<ObjType>("combo_rune_lava") {
            this += objs.earth_rune
            this += objs.fire_rune
        }
    }
}
