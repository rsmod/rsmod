package org.rsmod.api.spells.runes.subs.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.obj.ObjType

internal typealias runesub_enums = RuneSubstituteEnums

internal object RuneSubstituteEnums : EnumReferences() {
    val runes: EnumType<ObjType, EnumType<Int, ObjType>> = find("rune_substitutes")
    val air_runes: EnumType<Int, ObjType> = find("air_rune_substitutes")
    val water_runes: EnumType<Int, ObjType> = find("water_rune_substitutes")
    val earth_runes: EnumType<Int, ObjType> = find("earth_rune_substitutes")
    val fire_runes: EnumType<Int, ObjType> = find("fire_rune_substitutes")
    val chaos_runes: EnumType<Int, ObjType> = find("chaos_rune_substitutes")
    val death_runes: EnumType<Int, ObjType> = find("death_rune_substitutes")
    val blood_runes: EnumType<Int, ObjType> = find("blood_rune_substitutes")
}

internal object RuneSubstituteEnumBuilder : EnumBuilder() {
    init {
        build<ObjType, EnumType<Int, ObjType>>("rune_substitutes") {
            this[objs.air_rune] = runesub_enums.air_runes
            this[objs.water_rune] = runesub_enums.water_runes
            this[objs.earth_rune] = runesub_enums.earth_runes
            this[objs.fire_rune] = runesub_enums.fire_runes
            this[objs.chaos_rune] = runesub_enums.chaos_runes
            this[objs.death_rune] = runesub_enums.death_runes
            this[objs.blood_rune] = runesub_enums.blood_runes
        }

        buildAutoInt<ObjType>("air_rune_substitutes") { this += objs.air_rune_nz }
        buildAutoInt<ObjType>("water_rune_substitutes") { this += objs.water_rune_nz }
        buildAutoInt<ObjType>("earth_rune_substitutes") { this += objs.earth_rune_nz }

        buildAutoInt<ObjType>("fire_rune_substitutes") {
            this += objs.fire_rune_nz
            this += objs.sunfire_rune
        }

        buildAutoInt<ObjType>("chaos_rune_substitutes") { this += objs.chaos_rune_nz }
        buildAutoInt<ObjType>("death_rune_substitutes") { this += objs.death_rune_nz }
        buildAutoInt<ObjType>("blood_rune_substitutes") { this += objs.blood_rune_nz }
    }
}
