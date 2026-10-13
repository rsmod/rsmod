package org.runerealm.api.spells.runes.compact.configs

import org.runerealm.api.type.refs.enums.EnumReferences
import org.runerealm.game.type.enums.EnumType
import org.runerealm.game.type.obj.ObjType

internal typealias compact_enums = CompactRuneEnums

internal object CompactRuneEnums : EnumReferences() {
    val compact_ids: EnumType<ObjType, Int> = find("rune_compact_ids")
}
