package org.rsmod.api.cache.types.varp

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.GameTestState
import org.rsmod.game.type.util.VarplayerCollisions

class VarpBitCollisionTest {
    @Test
    fun GameTestState.`detect varbit children collisions`() = runBasicGameTest {
        val varps = cacheTypes.varps.filterValues { it.bitProtect }
        val varbits = cacheTypes.varbits.filterValues { varps.containsKey(it.baseVar.id) }

        val collisions = VarplayerCollisions.detect(varps.values, varbits.values)
        assertEquals(emptyList<VarplayerCollisions.Error>(), collisions.take(50))
    }
}
