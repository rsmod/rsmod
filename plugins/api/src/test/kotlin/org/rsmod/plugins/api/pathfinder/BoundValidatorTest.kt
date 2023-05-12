package org.rsmod.plugins.api.pathfinder

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.rsmod.game.map.Coordinates
import org.rsmod.game.map.entity.obj.ObjectEntity
import org.rsmod.game.model.client.Entity
import org.rsmod.game.model.client.PlayerEntity
import org.rsmod.game.pathfinder.collision.CollisionFlagMap
import org.rsmod.game.pathfinder.flag.BlockAccessFlag.BLOCK_EAST
import org.rsmod.game.pathfinder.flag.BlockAccessFlag.BLOCK_NORTH
import org.rsmod.game.pathfinder.flag.BlockAccessFlag.BLOCK_SOUTH
import org.rsmod.game.pathfinder.flag.BlockAccessFlag.BLOCK_WEST
import org.rsmod.plugins.api.map.GameObject
import org.rsmod.plugins.cache.config.obj.ObjectTypeBuilder

class BoundValidatorTest {

    @Test
    fun testEntityTouchesEntity() {
        val validator = createValidator { allocateIfAbsent(3200, 3200, 0) }
        val entity = createEntity()
        val target = createEntity()
        assertFalse(validator.touches(entity.withCoords(3200, 3200), target.withCoords(3200, 3200)))
        assertFalse(validator.touches(entity.withCoords(3200, 3200), target.withCoords(3200, 3202)))
        assertFalse(validator.touches(entity.withCoords(3200, 3200), target.withCoords(3202, 3200)))
        assertFalse(validator.touches(entity.withCoords(3200, 3200), target.withCoords(3202, 3202)))
        assertTrue(validator.touches(entity.withCoords(3200, 3200), target.withCoords(3200, 3201)))
        assertTrue(validator.touches(entity.withCoords(3200, 3200), target.withCoords(3201, 3200)))
        assertTrue(validator.touches(entity.withCoords(3201, 3200), target.withCoords(3200, 3200)))
        assertTrue(validator.touches(entity.withCoords(3200, 3201), target.withCoords(3200, 3200)))
    }

    @Test
    fun testEntityCollidesWithEntity() {
        val validator = createValidator { allocateIfAbsent(3200, 3200, 0) }
        val entity = createEntity()
        val target = createEntity()
        assertFalse(validator.collides(entity.withCoords(3200, 3200), target.withCoords(3200, 3201)))
        assertFalse(validator.collides(entity.withCoords(3200, 3200), target.withCoords(3201, 3200)))
        assertFalse(validator.collides(entity.withCoords(3200, 3200), target.withCoords(3201, 3201)))
        assertTrue(validator.collides(entity.withCoords(3200, 3200), target.withCoords(3200, 3200)))
    }

    @Test
    fun testEntityTouchesGameObject() {
        val validator = createValidator { allocateIfAbsent(3200, 3200, 0) }
        val entity = createEntity()
        entity.coords = Coordinates(3200, 3200)
        assertFalse(validator.touches(entity, createGameObject(3200, 3200)))
        assertFalse(validator.touches(entity, createGameObject(3202, 3200)))
        assertFalse(validator.touches(entity, createGameObject(3200, 3202)))
        assertFalse(validator.touches(entity, createGameObject(3202, 3202)))
        assertTrue(validator.touches(entity, createGameObject(3201, 3200)))
        assertTrue(validator.touches(entity, createGameObject(3200, 3201)))
    }

    @Test
    fun testEntityTouchesStrictGameObjectBlockNorth() {
        val validator = createValidator { allocateIfAbsent(3200, 3200, 0) }
        val entity = createEntity()
        val target = createGameObject(3204, 3204) { blockApproach = BLOCK_NORTH }
        assertFalse(validator.touchesStrict(entity.withCoords(3204, 3205), target))
        assertTrue(validator.touchesStrict(entity.withCoords(3205, 3204), target))
        assertTrue(validator.touchesStrict(entity.withCoords(3204, 3203), target))
        assertTrue(validator.touchesStrict(entity.withCoords(3203, 3204), target))
    }

    @Test
    fun testEntityTouchesStrictGameObjectBlockEast() {
        val validator = createValidator { allocateIfAbsent(3200, 3200, 0) }
        val entity = createEntity()
        val target = createGameObject(3204, 3204) { blockApproach = BLOCK_EAST }
        assertTrue(validator.touchesStrict(entity.withCoords(3204, 3205), target))
        assertFalse(validator.touchesStrict(entity.withCoords(3205, 3204), target))
        assertTrue(validator.touchesStrict(entity.withCoords(3204, 3203), target))
        assertTrue(validator.touchesStrict(entity.withCoords(3203, 3204), target))
    }

    @Test
    fun testEntityTouchesStrictGameObjectBlockSouth() {
        val validator = createValidator { allocateIfAbsent(3200, 3200, 0) }
        val entity = createEntity()
        val target = createGameObject(3204, 3204) { blockApproach = BLOCK_SOUTH }
        assertTrue(validator.touchesStrict(entity.withCoords(3204, 3205), target))
        assertTrue(validator.touchesStrict(entity.withCoords(3205, 3204), target))
        assertFalse(validator.touchesStrict(entity.withCoords(3204, 3203), target))
        assertTrue(validator.touchesStrict(entity.withCoords(3203, 3204), target))
    }

    @Test
    fun testEntityTouchesStrictGameObjectBlockWest() {
        val validator = createValidator { allocateIfAbsent(3200, 3200, 0) }
        val entity = createEntity()
        val target = createGameObject(3204, 3204) { blockApproach = BLOCK_WEST }
        assertTrue(validator.touchesStrict(entity.withCoords(3204, 3205), target))
        assertTrue(validator.touchesStrict(entity.withCoords(3205, 3204), target))
        assertTrue(validator.touchesStrict(entity.withCoords(3204, 3203), target))
        assertFalse(validator.touchesStrict(entity.withCoords(3203, 3204), target))
    }

    @Test
    fun testEntityCollidesWithGameObject() {
        val validator = createValidator { allocateIfAbsent(3200, 3200, 0) }
        val entity = createEntity()
        assertFalse(validator.collides(entity.withCoords(3200, 3200), createGameObject(3200, 3201)))
        assertFalse(validator.collides(entity.withCoords(3200, 3200), createGameObject(3201, 3200)))
        assertFalse(validator.collides(entity.withCoords(3200, 3200), createGameObject(3201, 3201)))
        assertTrue(validator.collides(entity.withCoords(3200, 3200), createGameObject(3200, 3200)))
    }

    private fun createValidator(init: CollisionFlagMap.() -> Unit): BoundValidator {
        return BoundValidator(CollisionFlagMap().apply(init))
    }

    private fun createEntity(): Entity {
        return PlayerEntity()
    }

    private fun createGameObject(
        x: Int,
        z: Int,
        level: Int = 0,
        init: ObjectTypeBuilder.() -> Unit = {}
    ): GameObject {
        val builder = ObjectTypeBuilder().apply { id = 0 }
        val type = builder.apply(init).build()
        val entity = ObjectEntity(type.id, shape = 10, rot = 0)
        return GameObject(type, Coordinates(x, z, level), entity)
    }

    private fun Entity.withCoords(x: Int, z: Int, level: Int = coords.level): Entity {
        coords = Coordinates(x, z, level)
        return this
    }
}
