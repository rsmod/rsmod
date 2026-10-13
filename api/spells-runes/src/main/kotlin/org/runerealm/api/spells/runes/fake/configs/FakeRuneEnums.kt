package org.runerealm.api.spells.runes.fake.configs

import org.runerealm.api.type.refs.enums.EnumReferences
import org.runerealm.game.type.enums.EnumType
import org.runerealm.game.type.obj.ObjType

internal typealias fake_enums = FakeRuneEnums

internal object FakeRuneEnums : EnumReferences() {
    val runes: EnumType<ObjType, ObjType> = find("fake_runes")
}
