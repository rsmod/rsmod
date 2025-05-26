package org.rsmod.api.player.worn

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.api.config.refs.objs
import org.rsmod.api.player.quiver
import org.rsmod.api.player.righthand
import org.rsmod.api.testing.GameTestState
import org.rsmod.events.EventBus
import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.obj.Wearpos

/* Obj transaction system is not thread-safe. */
@Execution(ExecutionMode.SAME_THREAD)
class WornUnequipOpTest {
    @Test
    fun GameTestState.`unequip standard obj`() = runBasicGameTest {
        withPlayerInit {
            inv[0] = InvObj(objs.beer)
            righthand = InvObj(objs.crystal_axe, vars = Int.MAX_VALUE)

            val operations = WornUnequipOp(cacheTypes.objs, EventBus())
            val result = operations.unequip(this, Wearpos.RightHand.slot, worn, inv)
            assertInstanceOf<WornUnequipResult.Success>(result)
            assertNull(righthand)
            assertEquals(InvObj(objs.crystal_axe, vars = Int.MAX_VALUE), inv[1])
            assertEquals(InvObj(objs.beer), inv[0])
        }
    }

    @Test
    fun GameTestState.`unequip obj into full inv`() = runBasicGameTest {
        withPlayerInit {
            repeat(inv.size) { inv[it] = InvObj(objs.beer) }
            righthand = InvObj(objs.crystal_axe)

            val operations = WornUnequipOp(cacheTypes.objs, EventBus())
            val result = operations.unequip(this, Wearpos.RightHand.slot, worn, inv)
            assertInstanceOf<WornUnequipResult.Fail>(result)
            assertEquals(InvObj(objs.crystal_axe), righthand)
            assertEquals(List(inv.size) { InvObj(objs.beer) }, inv.toList())
        }
    }

    @Test
    fun GameTestState.`unequip stackable obj into max stack of same type`() = runBasicGameTest {
        withPlayerInit {
            inv[3] = InvObj(objs.rune_arrow, count = Int.MAX_VALUE)
            quiver = InvObj(objs.rune_arrow, count = 50)

            val operations = WornUnequipOp(cacheTypes.objs, EventBus())
            val result = operations.unequip(this, Wearpos.Quiver.slot, worn, inv)
            assertInstanceOf<WornUnequipResult.Fail>(result)
            assertEquals(InvObj(objs.rune_arrow, count = 50), quiver)
            assertEquals(InvObj(objs.rune_arrow, count = Int.MAX_VALUE), inv[3])
        }
    }

    @Test
    fun GameTestState.`unequip max stack obj into stack of same type`() = runBasicGameTest {
        withPlayerInit {
            inv[3] = InvObj(objs.rune_arrow, count = 50)
            quiver = InvObj(objs.rune_arrow, count = Int.MAX_VALUE)

            val operations = WornUnequipOp(cacheTypes.objs, EventBus())
            val result = operations.unequip(this, Wearpos.Quiver.slot, worn, inv)
            assertInstanceOf<WornUnequipResult.Fail>(result)
            assertEquals(InvObj(objs.rune_arrow, count = Int.MAX_VALUE), quiver)
            assertEquals(InvObj(objs.rune_arrow, count = 50), inv[3])
        }
    }

    @Test
    fun GameTestState.`unequip stackable obj into full inv and one of same type`() =
        runBasicGameTest {
            withPlayerInit {
                repeat(inv.size) { inv[it] = InvObj(objs.beer) }
                inv[3] = InvObj(objs.rune_arrow, count = 50)
                quiver = InvObj(objs.rune_arrow, count = 50)

                val operations = WornUnequipOp(cacheTypes.objs, EventBus())
                val result = operations.unequip(this, Wearpos.Quiver.slot, worn, inv)
                assertInstanceOf<WornUnequipResult.Success>(result)
                assertEquals(InvObj(objs.rune_arrow, count = 100), inv[3])
                assertNull(quiver)
            }
        }
}
