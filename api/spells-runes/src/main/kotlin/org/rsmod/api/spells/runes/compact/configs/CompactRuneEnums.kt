package org.rsmod.api.spells.runes.compact.configs

import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.obj.ObjType

internal typealias compact_enums = CompactRuneEnums

internal object CompactRuneEnums : EnumReferences() {
    val compact_ids: EnumType<ObjType, Int> = find("rune_compact_ids")
}
