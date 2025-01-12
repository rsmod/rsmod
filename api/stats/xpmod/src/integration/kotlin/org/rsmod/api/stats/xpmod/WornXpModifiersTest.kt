package org.rsmod.api.stats.xpmod

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.config.refs.params
import org.rsmod.api.testing.GameTestState

class WornXpModifiersTest {
    @Test
    fun GameTestState.`ensure objs with modifiers have required params`() {
        val types =
            cacheTypes.objs.values.filter {
                it.hasParam(params.xpmod_stat) || it.hasParam(params.xpmod_percent)
            }
        for (type in types) {
            val map = checkNotNull(type.paramMap)
            assertTrue(params.xpmod_stat in map) { "Type must have `xpmod_stat` set: $type" }
            assertTrue(params.xpmod_percent in map) { "Type must have `xpmod_percent` set: $type" }
        }
    }
}
