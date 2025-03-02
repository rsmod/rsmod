package org.rsmod.api.cache.types

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.GameTestState

class TypeIdentityHashCollisionTest {
    @Test
    fun GameTestState.`detect component type collisions`() = runBasicGameTest {
        val types = cacheTypes.components.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect interface type collisions`() = runBasicGameTest {
        val types = cacheTypes.interfaces.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect enum type collisions`() = runBasicGameTest {
        val types = cacheTypes.enums.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect loc type collisions`() = runBasicGameTest {
        val types = cacheTypes.locs.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect npc type collisions`() = runBasicGameTest {
        val types = cacheTypes.npcs.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect obj type collisions`() = runBasicGameTest {
        val types = cacheTypes.objs.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect param type collisions`() = runBasicGameTest {
        val types = cacheTypes.params.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect varbit type collisions`() = runBasicGameTest {
        val types = cacheTypes.varbits.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect varp type collisions`() = runBasicGameTest {
        val types = cacheTypes.varps.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect inv type collisions`() = runBasicGameTest {
        val types = cacheTypes.invs.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect seq type collisions`() = runBasicGameTest {
        val types = cacheTypes.seqs.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect font metrics type collisions`() = runBasicGameTest {
        val types = cacheTypes.fonts.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect struct type collisions`() = runBasicGameTest {
        val types = cacheTypes.structs.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect spotanim type collisions`() = runBasicGameTest {
        val types = cacheTypes.spotanims.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }

    @Test
    fun GameTestState.`detect hitmark type collisions`() = runBasicGameTest {
        val types = cacheTypes.hitmarks.values
        val grouped = types.groupBy { it.computeIdentityHash() }
        val collisions = grouped.filter { it.value.size > 1 }
        assertTrue(collisions.isEmpty()) { "Type collision detected: $collisions." }
        assertEquals(grouped.size, types.size)
    }
}
