package org.rsmod.api.spells.runes.staves.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.obj.ObjType

internal typealias staff_enums = StaffSubstituteEnums

internal object StaffSubstituteEnums : EnumReferences() {
    val staves: EnumType<ObjType, EnumType<Int, ObjType>> = find("staff_substitutes")
    val guthix_staff: EnumType<Int, ObjType> = find("guthix_staff_substitutes")
    val zamorak_staff: EnumType<Int, ObjType> = find("zamorak_staff_substitutes")
    val saradomin_staff: EnumType<Int, ObjType> = find("saradomin_staff_substitutes")
    val slayer_staff: EnumType<Int, ObjType> = find("slayer_staff_substitutes")
    val iban_staff: EnumType<Int, ObjType> = find("iban_staff_substitutes")
}

internal object StaffSubstituteEnumBuilder : EnumBuilder() {
    init {
        build<ObjType, EnumType<Int, ObjType>>("staff_substitutes") {
            this[objs.guthix_staff] = staff_enums.guthix_staff
            this[objs.zamorak_staff] = staff_enums.zamorak_staff
            this[objs.saradomin_staff] = staff_enums.saradomin_staff
            this[objs.slayers_staff] = staff_enums.slayer_staff
            this[objs.ibans_staff] = staff_enums.iban_staff
        }

        buildAutoInt<ObjType>("guthix_staff_substitutes") {
            this += objs.void_knight_mace
            this += objs.void_knight_mace_l
            this += objs.staff_of_balance
        }

        // Note: Zamorak staff has a special condition for righthand objs that have `param_1737`
        // set. Since this is the only staff that has this condition, we are simply storing
        // each one of those items manually in this enum.
        buildAutoInt<ObjType>("zamorak_staff_substitutes") {
            this += objs.staff_of_the_dead
            this += objs.toxic_staff_of_the_dead
            this += objs.toxic_staff_uncharged
            this += objs.thammarons_sceptre
            this += objs.thammarons_sceptre_a
            this += objs.thammarons_sceptre_au
            this += objs.accursed_sceptre
            this += objs.accursed_sceptre_a
            this += objs.accursed_sceptre_au
        }

        buildAutoInt<ObjType>("saradomin_staff_substitutes") { this += objs.staff_of_light }

        buildAutoInt<ObjType>("slayer_staff_substitutes") {
            this += objs.slayers_staff_e
            this += objs.staff_of_the_dead
            this += objs.toxic_staff_uncharged
            this += objs.toxic_staff_of_the_dead
            this += objs.staff_of_light
            this += objs.staff_of_balance
        }

        buildAutoInt<ObjType>("iban_staff_substitutes") { this += objs.ibans_staff_u }
    }
}
