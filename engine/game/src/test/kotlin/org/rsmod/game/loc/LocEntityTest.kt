package org.rsmod.game.loc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.rsmod.game.loc.LocEntity.Companion.ANGLE_BIT_MASK
import org.rsmod.game.loc.LocEntity.Companion.ID_BIT_MASK
import org.rsmod.game.loc.LocEntity.Companion.SHAPE_BIT_MASK

class LocEntityTest {
    @Test
    fun `construct every loc entity combination`() {
        for (angle in 0..ANGLE_BIT_MASK) {
            for (shape in 0..SHAPE_BIT_MASK) {
                for (id in 0..ID_BIT_MASK) {
                    val entity = LocEntity(id, shape, angle)
                    assertEquals(id, entity.id)
                    assertEquals(shape, entity.shape)
                    assertEquals(angle, entity.angle)
                }
            }
        }
    }

    @Test
    fun `deconstruct every loc entity combination`() {
        for (angle in 0..ANGLE_BIT_MASK) {
            for (shape in 0..SHAPE_BIT_MASK) {
                for (id in 0..ID_BIT_MASK) {
                    val entity = LocEntity(id, shape, angle)
                    val (c1, c2, c3) = entity
                    assertEquals(id, c1)
                    assertEquals(shape, c2)
                    assertEquals(angle, c3)
                }
            }
        }
    }

    @Test
    fun `fail to construct on out of bound parameter`() {
        assertThrows<IllegalArgumentException> { LocEntity(ID_BIT_MASK + 1, 0, 0) }
        assertThrows<IllegalArgumentException> { LocEntity(0, SHAPE_BIT_MASK + 1, 0) }
        assertThrows<IllegalArgumentException> { LocEntity(0, 0, ANGLE_BIT_MASK + 1) }
        assertThrows<IllegalArgumentException> { LocEntity(-1, 0, 0) }
        assertThrows<IllegalArgumentException> { LocEntity(0, -1, 0) }
        assertThrows<IllegalArgumentException> { LocEntity(0, 0, -1) }
    }
}
