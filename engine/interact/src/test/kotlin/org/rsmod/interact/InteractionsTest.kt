package org.rsmod.interact

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InteractionsTest {
    @Test
    fun `trigger wandering banker ap followed up by op`() {
        val player = entity(3137, 3629)
        val mob = entity(3134, 3629)
        with(Interactions) {
            val early =
                earlyStep(
                    target = InteractionTarget.Pathing,
                    hasScriptOp = true,
                    validOpLine = player.inOpRange(mob),
                    hasScriptAp = true,
                    validApLine = player.inApRange(mob, 10) && player.hasLos(mob, true),
                )
            assertEquals(InteractionStep.TriggerScriptAp, early)

            val late =
                lateStep(
                    target = InteractionTarget.Pathing,
                    hasMoved = true,
                    hasScriptOp = true,
                    validOpLine = player.inOpRange(mob),
                    hasScriptAp = true,
                    validApLine = player.inApRange(mob, 2) && player.hasLos(mob, true),
                )
            assertEquals(InteractionStep.Continue, late)
        }
        player.move(3135, 3629)
        with(Interactions) {
            val early =
                earlyStep(
                    target = InteractionTarget.Pathing,
                    hasScriptOp = true,
                    validOpLine = player.inOpRange(mob),
                    hasScriptAp = true,
                    validApLine = player.inApRange(mob, 2) && player.hasLos(mob, true),
                )
            assertEquals(InteractionStep.TriggerScriptOp, early)
        }
    }

    @Test
    fun `double combat triggers`() {
        val player = entity(3181, 3436)
        val mob = entity(3182, 3432)
        with(Interactions) {
            val early =
                earlyStep(
                    target = InteractionTarget.Pathing,
                    hasScriptOp = true,
                    validOpLine = player.inOpRange(mob),
                    hasScriptAp = true,
                    validApLine = player.inApRange(mob, 10) && player.hasLos(mob, true),
                )
            assertEquals(InteractionStep.TriggerScriptAp, early)

            player.move(3182, 3434)
            val late =
                lateStep(
                    target = InteractionTarget.Pathing,
                    hasMoved = true,
                    hasScriptOp = true,
                    validOpLine = player.inOpRange(mob),
                    hasScriptAp = true,
                    validApLine = player.inApRange(mob, -1) && player.hasLos(mob, true),
                )
            assertEquals(InteractionStep.Continue, late)
        }
        player.move(3182, 3433)
        with(Interactions) {
            val early =
                earlyStep(
                    target = InteractionTarget.Pathing,
                    hasScriptOp = true,
                    validOpLine = player.inOpRange(mob),
                    hasScriptAp = true,
                    validApLine = player.inApRange(mob, -1) && player.hasLos(mob, true),
                )
            assertEquals(InteractionStep.TriggerScriptOp, early)
        }
    }

    @Test
    fun `trigger npc engine op preemptively`() {
        val player = entity(3210, 3214)
        val mob = entity(3210, 3212)
        with(Interactions) {
            val early =
                earlyStep(
                    target = InteractionTarget.Pathing,
                    hasScriptOp = false,
                    validOpLine = player.inOpRange(mob),
                    hasScriptAp = false,
                    validApLine = player.inApRange(mob, 10) && player.hasLos(mob, true),
                )
            assertEquals(InteractionStep.TriggerEngineAp, early)

            player.move(3210, 3213)
            val late =
                lateStep(
                    target = InteractionTarget.Pathing,
                    hasMoved = true,
                    hasScriptOp = false,
                    validOpLine = player.inOpRange(mob),
                    hasScriptAp = false,
                    validApLine = player.inApRange(mob, -1) && player.hasLos(mob, true),
                )
            assertEquals(InteractionStep.TriggerEngineOp, late)
        }
    }

    @Test
    fun `trigger bed engine op after arrival`() {
        val player = entity(3209, 3215)
        val bed = entity(3211, 3214, width = 3, length = 2)
        with(Interactions) {
            val early =
                earlyStep(
                    target = InteractionTarget.Static,
                    hasScriptOp = false,
                    validOpLine = player.inOpRange(bed),
                    hasScriptAp = false,
                    validApLine = player.inApRange(bed, 10) && player.hasLos(bed, true),
                )
            assertEquals(InteractionStep.TriggerEngineAp, early)

            player.move(3210, 3215)
            val late =
                lateStep(
                    target = InteractionTarget.Static,
                    hasMoved = true,
                    hasScriptOp = false,
                    validOpLine = player.inOpRange(bed),
                    hasScriptAp = false,
                    validApLine = player.inApRange(bed, -1) && player.hasLos(bed, true),
                )
            assertEquals(InteractionStep.Continue, late)
        }
        with(Interactions) {
            val early =
                earlyStep(
                    target = InteractionTarget.Static,
                    hasScriptOp = false,
                    validOpLine = player.inOpRange(bed),
                    hasScriptAp = false,
                    validApLine = player.inApRange(bed, -1) && player.hasLos(bed, true),
                )
            assertEquals(InteractionStep.Continue, early)

            val late =
                lateStep(
                    target = InteractionTarget.Static,
                    hasMoved = false,
                    hasScriptOp = false,
                    validOpLine = player.inOpRange(bed),
                    hasScriptAp = false,
                    validApLine = player.inApRange(bed, -1) && player.hasLos(bed, true),
                )
            assertEquals(InteractionStep.TriggerEngineOp, late)
        }
    }

    @Test
    fun `reach banker ap script behind booth`() {
        val player = entity(3207, 3219)
        val banker = entity(3208, 3222)
        with(Interactions) {
            val early =
                earlyStep(
                    target = InteractionTarget.Pathing,
                    hasScriptOp = false,
                    validOpLine = player.inOpRange(banker),
                    hasScriptAp = true,
                    validApLine = player.inApRange(banker, 10) && player.hasLos(banker, false),
                )
            assertEquals(InteractionStep.Continue, early)

            player.move(3208, 3220)
            val late =
                lateStep(
                    target = InteractionTarget.Pathing,
                    hasMoved = true,
                    hasScriptOp = false,
                    validOpLine = player.inOpRange(banker),
                    hasScriptAp = true,
                    validApLine = player.inApRange(banker, 2) && player.hasLos(banker, true),
                )
            assertEquals(InteractionStep.TriggerScriptAp, late)
        }
    }

    @Test
    fun `cannot reach banker ap script behind wall`() {
        val player = entity(3206, 3225)
        val banker = entity(3208, 3222)
        val hasLos = false
        with(Interactions) {
            val early =
                earlyStep(
                    target = InteractionTarget.Pathing,
                    hasScriptOp = true,
                    validOpLine = player.inOpRange(banker),
                    hasScriptAp = true,
                    validApLine = player.inApRange(banker, 2) && hasLos,
                )
            player.move(3207, 3225)
            val late =
                lateStep(
                    target = InteractionTarget.Pathing,
                    hasMoved = true,
                    hasScriptOp = true,
                    validOpLine = player.inOpRange(banker),
                    hasScriptAp = true,
                    validApLine = player.inApRange(banker, 2) && hasLos,
                )
            assertEquals(InteractionStep.Continue, early)
            assertEquals(InteractionStep.Continue, late)
        }
        player.move(3208, 3224)
        with(Interactions) {
            val step =
                earlyStep(
                    target = InteractionTarget.Pathing,
                    hasScriptOp = false,
                    validOpLine = player.inOpRange(banker),
                    hasScriptAp = true,
                    validApLine = player.inApRange(banker, 2) && hasLos,
                )
            assertEquals(InteractionStep.Continue, step)
            // At this point the game server will check if player has moved,
            // has pending movement or had a trigger-type step. If none of those
            // are true, it performs an engine-level "I can't reach that.".
        }
    }

    @Test
    fun `entity is under`() {
        val dummy1 = entity(3200, 3200)
        val dummy2 = entity(3200, 3200)
        assertTrue(dummy1.isUnder(dummy2))
        dummy2.move(3201, 3200)
        assertFalse(dummy1.isUnder(dummy2))
        dummy2.move(3199, 3199)
        assertFalse(dummy1.isUnder(dummy2))
        dummy2.move(3199, 3201)
        assertFalse(dummy1.isUnder(dummy2))
        dummy2.move(3201, 3199)
        assertFalse(dummy1.isUnder(dummy2))
        dummy2.move(3201, 3201)
        assertFalse(dummy1.isUnder(dummy2))
    }

    @Test
    fun `entity is diagonal`() {
        val dummy1 = entity(3200, 3200)
        val dummy2 = entity(3200, 3200)
        assertFalse(dummy1.isDiagonal(dummy2))
        dummy2.move(3201, 3200)
        assertFalse(dummy1.isDiagonal(dummy2))
        dummy2.move(3199, 3199)
        assertTrue(dummy1.isDiagonal(dummy2))
        dummy2.move(3199, 3201)
        assertTrue(dummy1.isDiagonal(dummy2))
        dummy2.move(3201, 3199)
        assertTrue(dummy1.isDiagonal(dummy2))
        dummy2.move(3201, 3201)
        assertTrue(dummy1.isDiagonal(dummy2))
    }

    @Test
    fun `entity in op range`() {
        val dummy1 = entity(3200, 3200)
        val dummy2 = entity(3200, 3200)
        assertFalse(dummy1.inOpRange(dummy2))
        dummy2.move(3201, 3200)
        assertTrue(dummy1.inOpRange(dummy2))
        dummy2.move(3201, 3201)
        assertFalse(dummy1.inOpRange(dummy2))
        dummy2.move(3199, 3200)
        assertTrue(dummy1.inOpRange(dummy2))
        dummy2.move(3202, 3202)
        assertFalse(dummy1.inOpRange(dummy2))
    }

    private fun entity(x: Int, z: Int, width: Int = 1, length: Int = 1): DummyEntity =
        DummyEntity(x, z, width, length)

    private data class DummyEntity(
        var x: Int = 0,
        var z: Int = 0,
        val width: Int = 1,
        val length: Int = 1,
    ) {
        fun move(x: Int, z: Int): DummyEntity {
            this.x = x
            this.z = z
            return this
        }

        @Suppress("unused")
        fun hasLos(other: DummyEntity, los: Boolean): Boolean {
            return los
        }

        fun inOpRange(other: DummyEntity, distance: Int = 1): Boolean {
            val diagonal = isDiagonal(other)
            if (diagonal) {
                return false
            }
            val under = isUnder(other)
            if (under) {
                return false
            }
            return inApRange(other, distance)
        }

        fun inApRange(other: DummyEntity, distance: Int): Boolean {
            val minX = other.x - distance
            val maxX = other.x + distance + (other.width - 1)
            val minZ = other.z - distance
            val maxZ = other.z + distance + (other.length - 1)
            return x in minX..maxX && z in minZ..maxZ
        }

        fun isDiagonal(
            other: DummyEntity,
            widthIncl: Int = other.width,
            lengthIncl: Int = other.length,
        ): Boolean {
            return x == other.x - 1 && z == other.z - 1 ||
                x == other.x - 1 && z == other.z + lengthIncl ||
                x == other.x + widthIncl && z == other.z - 1 ||
                x == other.x + widthIncl && z == other.z + lengthIncl
        }

        fun isUnder(
            other: DummyEntity,
            widthExcl: Int = other.width - 1,
            lengthExcl: Int = other.length - 1,
        ): Boolean = x in other.x..other.x + widthExcl && z in other.z..other.z + lengthExcl
    }
}
