package org.rsmod.content.other.canoe.configs

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.config.refs.content
import org.rsmod.api.testing.GameTestState
import org.rsmod.game.type.obj.ObjType

class CanoeConfigTest {
    @Test
    fun GameTestState.`ensure all axe objs have canoe shaping anim`() = runBasicGameTest {
        val axes = cacheTypes.objs.values.filter { it.isContentType(content.woodcutting_axe) }
        val required = cacheTypes.enums[canoe_enums.shaping_axe_anims].keys.map(ObjType::id)
        val missing = axes.filter { it.id !in required }.map { it as ObjType }
        assertEquals(emptyList<ObjType>(), missing) {
            "Found missing axe objs in `shaping_axe_anims` enum:"
        }
    }

    @Test
    fun GameTestState.`ensure all axe objs have station axe rates`() = runBasicGameTest {
        val axes = cacheTypes.objs.values.filter { it.isContentType(content.woodcutting_axe) }
        val required = cacheTypes.enums[canoe_enums.station_axe_rates].keys.map(ObjType::id)
        val missing = axes.filter { it.id !in required }.map { it as ObjType }
        assertEquals(emptyList<ObjType>(), missing) {
            "Found missing axe objs in `station_axe_rates` enum:"
        }
    }

    @Test
    fun GameTestState.`ensure all axe objs have shaping axe rates`() = runBasicGameTest {
        val axes = cacheTypes.objs.values.filter { it.isContentType(content.woodcutting_axe) }
        val required = cacheTypes.enums[canoe_enums.shaping_axe_rates].keys.map(ObjType::id)
        val missing = axes.filter { it.id !in required }.map { it as ObjType }
        assertEquals(emptyList<ObjType>(), missing) {
            "Found missing axe objs in `shaping_axe_rates` enum:"
        }
    }
}
