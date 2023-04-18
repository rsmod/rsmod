package org.rsmod.game.map.entity.obj

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.game.map.entity.obj.ObjectEntity.Companion.ID_BIT_MASK
import org.rsmod.game.map.entity.obj.ObjectEntity.Companion.ROT_BIT_MASK
import org.rsmod.game.map.entity.obj.ObjectEntity.Companion.SHAPE_BIT_MASK

class ObjectEntityTest {

    @Test
    fun testConstruct() {
        for (rot in 0..ROT_BIT_MASK) {
            for (shape in 0..SHAPE_BIT_MASK) {
                for (id in 0..ID_BIT_MASK) {
                    val entity = ObjectEntity(id, shape, rot)
                    assertEquals(id, entity.id)
                    assertEquals(shape, entity.shape)
                    assertEquals(rot, entity.rot)
                }
            }
        }
    }

    @Test
    fun testDeconstruct() {
        for (rot in 0..ROT_BIT_MASK) {
            for (shape in 0..SHAPE_BIT_MASK) {
                for (id in 0..ID_BIT_MASK) {
                    val entity = ObjectEntity(id, shape, rot)
                    val (c1, c2, c3) = entity
                    assertEquals(id, c1)
                    assertEquals(shape, c2)
                    assertEquals(rot, c3)
                }
            }
        }
    }

    @Test
    fun testConstructOutOfBounds() {
        assertThrows<IllegalArgumentException> { ObjectKey(ID_BIT_MASK + 1, 0, 0) }
        assertThrows<IllegalArgumentException> { ObjectKey(0, SHAPE_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { ObjectKey(0, 0, ROT_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { ObjectKey(-1, 0, 0) }
        assertThrows<IllegalArgumentException> { ObjectKey(0, -1, 0) }
        assertThrows<IllegalArgumentException> { ObjectKey(0, 0, -1) }
    }
}
