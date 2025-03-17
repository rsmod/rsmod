package org.rsmod.api.combat.spells.autocast.configs

import org.rsmod.api.config.refs.objs
import org.rsmod.api.type.builders.enums.EnumBuilder
import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.obj.ObjType

internal typealias autocast_enums = AutocastEnums

internal object AutocastEnums : EnumReferences() {
    val spells = find<Int, ObjType>("autocast_spells")
    val restricted_spells = find<ObjType, Boolean>("autocast_restricted_spells")
}

internal object AutocastEnumBuilder : EnumBuilder() {
    init {
        // Any autocast spell that can only be cast by specific a staff (or staves) is considered
        // "restricted." The staves are usually registered in [AutocastObjEditor].
        build<ObjType, Boolean>("autocast_restricted_spells") {
            this[objs.spell_crumble_undead] = true
            this[objs.spell_iban_blast] = true
            this[objs.spell_magic_dart] = true
            this[objs.spell_flames_of_zamorak] = true
            this[objs.spell_saradomin_strike] = true
            this[objs.spell_claws_of_guthix] = true
        }
    }
}
