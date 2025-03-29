package org.rsmod.api.spells.configs

import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.obj.ObjType

internal typealias spell_enums = SpellEnums

internal object SpellEnums : EnumReferences() {
    val spellbooks: EnumType<Int, EnumType<Int, ObjType>> = find("spellbooks")
    val autocast_spells = find<Int, ObjType>("autocast_spells")
}
