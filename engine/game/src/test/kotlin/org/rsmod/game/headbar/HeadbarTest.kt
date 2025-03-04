package org.rsmod.game.headbar

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.game.entity.util.PathingEntityCommon

class HeadbarTest {
    @Test
    fun `create headbar from no source`() {
        val headbar =
            Headbar.fromNoSource(
                self = 1,
                public = null,
                startFill = 50,
                endFill = 25,
                startTime = 10,
                endTime = 20,
            )
        assertEquals(1, headbar.self)
        assertEquals(Headbar.HEADBAR_ID_BIT_MASK.toInt(), headbar.public)
        assertEquals(50, headbar.startFill)
        assertEquals(25, headbar.endFill)
        assertEquals(10, headbar.startTime)
        assertEquals(20, headbar.endTime)

        val expectedSourceSlot = Headbar.SOURCE_SLOT_BIT_MASK.toInt()
        assertEquals(expectedSourceSlot, headbar.sourceSlot)

        assertTrue(headbar.isNoSource)
        assertFalse(headbar.isPlayerSource)
        assertFalse(headbar.isNpcSource)
    }

    @Test
    fun `create headbar with npc as the source`() {
        val npcSlot = 30
        val headbar =
            Headbar.fromNpcSource(
                self = 2,
                public = 3,
                startFill = 75,
                endFill = 50,
                startTime = 15,
                endTime = 25,
                slotId = npcSlot,
            )
        assertEquals(2, headbar.self)
        assertEquals(3, headbar.public)
        assertEquals(75, headbar.startFill)
        assertEquals(50, headbar.endFill)
        assertEquals(15, headbar.startTime)
        assertEquals(25, headbar.endTime)

        val expectedSourceSlot = npcSlot
        assertEquals(expectedSourceSlot, headbar.sourceSlot)
        assertEquals(npcSlot, headbar.npcSlot)

        assertTrue(headbar.isNpcSource)
        assertFalse(headbar.isPlayerSource)
        assertFalse(headbar.isNoSource)
    }

    @Test
    fun `create headbar with player as the source`() {
        val playerSlot = 15
        val headbar =
            Headbar.fromPlayerSource(
                self = 4,
                public = 5,
                startFill = 100,
                endFill = 75,
                startTime = 20,
                endTime = 30,
                slotId = playerSlot,
            )
        assertEquals(4, headbar.self)
        assertEquals(5, headbar.public)
        assertEquals(100, headbar.startFill)
        assertEquals(75, headbar.endFill)
        assertEquals(20, headbar.startTime)
        assertEquals(30, headbar.endTime)

        val expectedSourceSlot = playerSlot + (PathingEntityCommon.INTERNAL_NPC_LIMIT + 1)
        assertEquals(expectedSourceSlot, headbar.sourceSlot)
        assertEquals(playerSlot, headbar.playerSlot)

        assertTrue(headbar.isPlayerSource)
        assertFalse(headbar.isNpcSource)
        assertFalse(headbar.isNoSource)
    }
}
