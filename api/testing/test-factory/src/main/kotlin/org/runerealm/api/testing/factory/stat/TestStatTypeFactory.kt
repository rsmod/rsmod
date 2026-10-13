package org.runerealm.api.testing.factory.stat

import org.runerealm.game.type.stat.StatType
import org.runerealm.game.type.stat.StatTypeBuilder

public class TestStatTypeFactory {
    public fun create(id: Int = 0, init: StatTypeBuilder.() -> Unit = {}): StatType =
        StatTypeBuilder("test_stat_type").apply(init).build(id)
}
