package org.runerealm.api.testing.factory.loc

import org.runerealm.game.loc.LocInfo
import org.runerealm.game.type.loc.LocTypeBuilder
import org.runerealm.game.type.loc.UnpackedLocType

public class TestLocTypeFactory {
    public fun create(loc: LocInfo, init: LocTypeBuilder.() -> Unit = {}): UnpackedLocType =
        create(loc.id, init)

    public fun create(id: Int = 0, init: LocTypeBuilder.() -> Unit = {}): UnpackedLocType {
        val builder =
            LocTypeBuilder().apply {
                internal = "test_loc_type"
                desc = "$this"
            }
        return builder.apply(init).build(id)
    }
}
