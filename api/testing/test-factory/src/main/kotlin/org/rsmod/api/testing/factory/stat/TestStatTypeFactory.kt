package org.rsmod.api.testing.factory.stat

import org.rsmod.game.type.stat.StatType
import org.rsmod.game.type.stat.StatTypeBuilder

public class TestStatTypeFactory {
    public fun create(id: Int = 0, init: StatTypeBuilder.() -> Unit = {}): StatType =
        StatTypeBuilder("test_stat_type").apply(init).build(id)
}
