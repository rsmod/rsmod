package org.rsmod.api.spells.runes.combo.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.obj.ObjType

internal typealias combo_enums = ComboRuneEnums

internal object ComboRuneEnums : EnumReferences() {
    val combos: EnumType<ObjType, EnumType<Int, ObjType>> = find("combo_runes")
    val air_runes: EnumType<Int, ObjType> = find("air_combo_runes")
    val water_runes: EnumType<Int, ObjType> = find("water_combo_runes")
    val earth_runes: EnumType<Int, ObjType> = find("earth_combo_runes")
    val fire_runes: EnumType<Int, ObjType> = find("fire_combo_runes")
    val chaos_runes: EnumType<Int, ObjType> = find("chaos_combo_runes")
    val death_runes: EnumType<Int, ObjType> = find("death_combo_runes")
    val blood_runes: EnumType<Int, ObjType> = find("blood_combo_runes")
}

internal object ComboRuneEnumBuilder : EnumBuilder() {
    init {
        build<ObjType, EnumType<Int, ObjType>>("combo_runes") {
            this[objs.air_rune] = combo_enums.air_runes
            this[objs.water_rune] = combo_enums.water_runes
            this[objs.earth_rune] = combo_enums.earth_runes
            this[objs.fire_rune] = combo_enums.fire_runes
            this[objs.chaos_rune] = combo_enums.chaos_runes
            this[objs.death_rune] = combo_enums.death_runes
            this[objs.blood_rune] = combo_enums.blood_runes
        }

        buildAutoInt<ObjType>("air_combo_runes") {
            this += objs.smoke_rune
            this += objs.mist_rune
            this += objs.dust_rune
            this += objs.air_rune_nz
        }

        buildAutoInt<ObjType>("water_combo_runes") {
            this += objs.steam_rune
            this += objs.mist_rune
            this += objs.mud_rune
            this += objs.water_rune_nz
        }

        buildAutoInt<ObjType>("earth_combo_runes") {
            this += objs.dust_rune
            this += objs.lava_rune
            this += objs.mud_rune
            this += objs.earth_rune_nz
        }

        buildAutoInt<ObjType>("fire_combo_runes") {
            this += objs.steam_rune
            this += objs.smoke_rune
            this += objs.lava_rune
            this += objs.sunfire_rune
            this += objs.fire_rune_nz
        }

        buildAutoInt<ObjType>("chaos_combo_runes") { this += objs.chaos_rune_nz }
        buildAutoInt<ObjType>("death_combo_runes") { this += objs.death_rune_nz }
        buildAutoInt<ObjType>("blood_combo_runes") { this += objs.blood_rune_nz }
    }
}
