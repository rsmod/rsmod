package org.rsmod.api.spells.runes.fake.configs

import org.rsmod.api.type.refs.enums.EnumReferences
import org.rsmod.game.type.enums.EnumType
import org.rsmod.game.type.obj.ObjType

internal typealias fake_enums = FakeRuneEnums

internal object FakeRuneEnums : EnumReferences() {
    val runes: EnumType<ObjType, ObjType> = find("fake_runes")
}
