package org.rsmod.game.hit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.game.entity.util.PathingEntityCommon

class HitmarkTest {
    @Test
    fun `create hitmark from no source`() {
        val hitmark =
            Hitmark.fromNoSource(self = 1, source = 2, public = null, damage = 100, delay = 5)
        assertEquals(1, hitmark.self)
        assertEquals(2, hitmark.source)
        assertEquals(Hitmark.HITMARK_ID_BIT_MASK.toInt(), hitmark.public)
        assertEquals(100, hitmark.damage)
        assertEquals(5, hitmark.delay)

        val expectedSourceSlot = Hitmark.SOURCE_SLOT_BIT_MASK.toInt()
        assertEquals(expectedSourceSlot, hitmark.sourceSlot)

        assertTrue(hitmark.isNoSource)
        assertFalse(hitmark.isPlayerSource)
        assertFalse(hitmark.isNpcSource)
    }

    @Test
    fun `create hitmark with npc as the source`() {
        val npcSlot = 50
        val hitmark =
            Hitmark.fromNpcSource(
                self = 3,
                source = 4,
                public = 5,
                damage = 200,
                delay = 6,
                slotId = npcSlot,
            )
        assertEquals(3, hitmark.self)
        assertEquals(4, hitmark.source)
        assertEquals(5, hitmark.public)
        assertEquals(200, hitmark.damage)
        assertEquals(6, hitmark.delay)

        val expectedSourceSlot = npcSlot
        assertEquals(expectedSourceSlot, hitmark.sourceSlot)
        assertEquals(npcSlot, hitmark.npcSlot)

        assertTrue(hitmark.isNpcSource)
        assertFalse(hitmark.isPlayerSource)
        assertFalse(hitmark.isNoSource)
    }

    @Test
    fun `create hitmark with player as the source`() {
        val playerSlot = 10
        val hitmark =
            Hitmark.fromPlayerSource(
                self = 7,
                source = 8,
                public = 9,
                damage = 300,
                delay = 7,
                slotId = playerSlot,
            )
        assertEquals(7, hitmark.self)
        assertEquals(8, hitmark.source)
        assertEquals(9, hitmark.public)
        assertEquals(300, hitmark.damage)
        assertEquals(7, hitmark.delay)

        val expectedSourceSlot = playerSlot + (PathingEntityCommon.INTERNAL_NPC_LIMIT + 1)
        assertEquals(expectedSourceSlot, hitmark.sourceSlot)
        assertEquals(playerSlot, hitmark.playerSlot)

        assertTrue(hitmark.isPlayerSource)
        assertFalse(hitmark.isNpcSource)
        assertFalse(hitmark.isNoSource)
    }

    @Test
    fun `copy with modified self field`() {
        val original =
            Hitmark.fromNoSource(self = 10, source = 11, public = null, damage = 150, delay = 8)
        val modified = original.copy(self = 20)
        assertEquals(20, modified.self)
        assertEquals(original.source, modified.source)
        assertEquals(original.public, modified.public)
        assertEquals(original.damage, modified.damage)
        assertEquals(original.delay, modified.delay)
        assertEquals(original.sourceSlot, modified.sourceSlot)
    }

    @Test
    fun `copy with modified source field`() {
        val original =
            Hitmark.fromNoSource(self = 10, source = 11, public = null, damage = 150, delay = 8)
        val modified = original.copy(source = 22)
        assertEquals(original.self, modified.self)
        assertEquals(22, modified.source)
        assertEquals(original.public, modified.public)
        assertEquals(original.damage, modified.damage)
        assertEquals(original.delay, modified.delay)
        assertEquals(original.sourceSlot, modified.sourceSlot)
    }

    @Test
    fun `copy with modified public field`() {
        val original =
            Hitmark.fromNoSource(self = 10, source = 11, public = null, damage = 150, delay = 8)
        val modified = original.copy(public = 33)
        assertEquals(original.self, modified.self)
        assertEquals(original.source, modified.source)
        assertEquals(33, modified.public)
        assertEquals(original.damage, modified.damage)
        assertEquals(original.delay, modified.delay)
        assertEquals(original.sourceSlot, modified.sourceSlot)
    }

    @Test
    fun `copy with modified damage field`() {
        val original =
            Hitmark.fromNoSource(self = 10, source = 11, public = null, damage = 150, delay = 8)
        val modified = original.copy(damage = 250)
        assertEquals(original.self, modified.self)
        assertEquals(original.source, modified.source)
        assertEquals(original.public, modified.public)
        assertEquals(250, modified.damage)
        assertEquals(original.delay, modified.delay)
        assertEquals(original.sourceSlot, modified.sourceSlot)
    }

    @Test
    fun `copy with modified delay field`() {
        val original =
            Hitmark.fromNoSource(self = 10, source = 11, public = null, damage = 150, delay = 8)
        val modified = original.copy(delay = 20)
        assertEquals(original.self, modified.self)
        assertEquals(original.source, modified.source)
        assertEquals(original.public, modified.public)
        assertEquals(original.damage, modified.damage)
        assertEquals(20, modified.delay)
        assertEquals(original.sourceSlot, modified.sourceSlot)
    }

    @Test
    fun `copy with modified sourceSlot field`() {
        val original =
            Hitmark.fromNoSource(self = 10, source = 11, public = null, damage = 150, delay = 8)
        val modified = original.copy(sourceSlot = 100)
        assertEquals(original.self, modified.self)
        assertEquals(original.source, modified.source)
        assertEquals(original.public, modified.public)
        assertEquals(original.damage, modified.damage)
        assertEquals(original.delay, modified.delay)
        assertEquals(100, modified.sourceSlot)
    }
}
