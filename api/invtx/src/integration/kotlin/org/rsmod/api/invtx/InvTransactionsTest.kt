package org.rsmod.api.invtx

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.api.config.refs.invs
import org.rsmod.api.config.refs.objs
import org.rsmod.api.testing.GameTestState
import org.rsmod.api.testing.assertions.assertNotNullContract
import org.rsmod.api.testing.assertions.assertTrueContract
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.obj.Dummyitem
import org.rsmod.game.type.obj.ObjType
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.objtx.TransactionResult
import org.rsmod.objtx.isOk

/**
 * The transaction system uses a shared list in the backend for performance enhancements. Due to
 * this, we require thread-safety and set the execution mode to fit that requirement.
 *
 * @see [org.rsmod.objtx.TransactionInventory.reusableIndexList]
 */
@Execution(ExecutionMode.SAME_THREAD)
class InvTransactionsTest {
    @Test
    fun GameTestState.`add obj successfully`() = runBasicGameTest {
        withPlayerInit {
            check(inv.isEmpty())
            invAdd(inv, objs.abyssal_whip, count = 2, slot = 3)
            assertEquals(objs.abyssal_whip.id, inv[3]?.id)
            assertEquals(objs.abyssal_whip.id, inv[4]?.id)
            assertEquals(2, inv.occupiedSpace())
        }
    }

    @Test
    fun GameTestState.`delete obj successfully`() = runBasicGameTest {
        withPlayerInit {
            inv[5] = objs.fire_cape.obj()
            val transaction = invDel(inv, objs.fire_cape, count = 2, strict = false).single()
            assertTrueContract(transaction.isOk())
            assertTrue(transaction.partialSuccess)
            assertEquals(1, transaction.completed)
            assertNull(inv[5])
            assertTrue(inv.isEmpty())
        }
    }

    @Test
    fun GameTestState.`swap inv objs`() = runBasicGameTest {
        val fromSlot = 10
        val toSlot = 4
        withPlayerInit {
            check(inv.isEmpty())
            inv[fromSlot] = objs.coins.obj(100_000)
            val transaction = invSwap(inv, fromSlot = fromSlot, intoSlot = toSlot).single()
            assertTrueContract(transaction.isOk())
            assertTrue(transaction.fullSuccess)
            assertNull(inv[fromSlot])
            assertNotNull(inv[toSlot])
            assertEquals(transaction.completed, 100_000)
            assertEquals(InvObj(objs.coins, 100_000), inv[toSlot])
        }
    }

    @Test
    fun GameTestState.`insert obj into bank`() = runBasicGameTest {
        withPlayerInit {
            check(inv.isEmpty())
            val bank = invMap.getOrPut(cacheTypes.invs[invs.bank])
            inv[0] = objs.abyssal_whip.obj()
            inv[1] = objs.abyssal_whip.obj()
            val transaction = invTransfer(inv, fromSlot = 0, count = 2, into = bank).single()
            assertTrueContract(transaction.isOk())
            assertTrue(transaction.fullSuccess)
            assertNull(inv[0])
            assertNull(inv[1])
            assertNotNull(bank[0])
            assertEquals(InvObj(objs.abyssal_whip, 2), bank[0])
            assertEquals(1, bank.occupiedSpace())
        }
    }

    @Test
    fun GameTestState.`withdraw obj from bank with placeholder`() = runBasicGameTest {
        withPlayerInit {
            check(inv.isEmpty())
            val placeholder = cacheTypes.objs[objs.abyssal_whip].placeholderlink
            val bank = invMap.getOrPut(cacheTypes.invs[invs.bank])
            bank[0] = objs.rune_arrow.obj(500)
            bank[1] = objs.abyssal_whip.obj()
            val transaction =
                invTransfer(bank, fromSlot = 1, count = 1, into = inv, placehold = true).single()
            assertTrueContract(transaction.isOk())
            assertTrue(transaction.fullSuccess)
            assertEquals(objs.abyssal_whip.obj(), inv[0])
            assertNotNull(bank[1])
            assertEquals(placeholder, bank[1]?.id)
            assertEquals(objs.rune_arrow.obj(500), bank[0])
            assertEquals(2, bank.occupiedSpace())
        }
    }

    @Test
    fun GameTestState.`equip stackable obj in worn with overflow leftover`() = runBasicGameTest {
        withPlayerInit {
            check(worn.isEmpty())
            check(inv.isEmpty())
            val addArrows = objs.rune_arrow.obj(5000)
            inv[3] = addArrows
            worn[Wearpos.Quiver.slot] = objs.rune_arrow.obj(Int.MAX_VALUE - 1000)
            val transaction =
                invSwap(
                        from = inv,
                        fromSlot = 3,
                        intoSlot = Wearpos.Quiver.slot,
                        into = worn,
                        mergeStacks = true,
                    )
                    .single()
            val quiver = worn[Wearpos.Quiver.slot]
            assertTrueContract(transaction.isOk())
            assertTrue(transaction.partialSuccess)
            assertEquals(objs.rune_arrow.obj(4000), inv[3])
            assertNotNull(quiver)
            assertEquals(Int.MAX_VALUE, quiver?.count)
        }
    }

    @Test
    fun GameTestState.`swap inv obj slots`() = runBasicGameTest {
        withPlayerInit {
            check(inv.isEmpty())
            val item1 = objs.berserker_ring.obj()
            val item2 = objs.coins.obj(100_000)
            val slot1 = 2
            val slot2 = 5
            inv[slot1] = item1
            inv[slot2] = item2
            check(inv[slot1] == item1)
            check(inv[slot2] == item2)
            val transaction = invSwap(from = inv, fromSlot = slot1, intoSlot = slot2).single()
            assertTrueContract(transaction.isOk())
            assertTrue(transaction.fullSuccess)
            assertEquals(item2, inv[slot1])
            assertEquals(item1, inv[slot2])
            assertEquals(2, inv.occupiedSpace())
        }
    }

    @Test
    fun GameTestState.`fail to add GraphicOnly dummyitem`() = runGameTest {
        val graphicOnly =
            objTypes.values.firstOrNull { it.resolvedDummyitem == Dummyitem.GraphicOnly }
        checkNotNull(graphicOnly) { "Cannot find valid `GraphicOnly` dummyitem." }

        player.clearAllInvs()

        val transaction = player.invAdd(player.inv, graphicOnly)
        assertInstanceOf<TransactionResult.RestrictedDummyitem>(transaction.err)
        assertEquals(0, player.inv.occupiedSpace())
    }

    @TestWithArgs(WearposProvider::class)
    fun `equip obj in wearpos`(wearpos: Wearpos, type: ObjType, count: Int, test: GameTestState) =
        test.runBasicGameTest {
            val invSlot = 5
            withPlayerInit {
                check(worn.isEmpty())
                check(inv.isEmpty())
                inv[invSlot] = type.obj(count)
                val transaction =
                    invSwap(
                            from = inv,
                            fromSlot = invSlot,
                            into = worn,
                            intoSlot = wearpos.slot,
                            mergeStacks = true,
                        )
                        .single()
                assertNotNullContract(transaction)
                assertTrueContract(transaction.isOk())
                assertTrue(transaction.fullSuccess)
                assertEquals(0, inv.occupiedSpace())
                assertEquals(1, worn.occupiedSpace())
                assertEquals(type.id, worn[wearpos.slot]?.id)
                assertEquals(count, worn[wearpos.slot]?.count)
            }
        }

    private object WearposProvider : TestArgsProvider {
        override fun args(): List<TestArgs> =
            listOf(
                TestArgs(Wearpos.Hat, objs.helm_of_neitiznot, 1),
                TestArgs(Wearpos.Back, objs.fire_cape, 1),
                TestArgs(Wearpos.RightHand, objs.abyssal_whip, 1),
                TestArgs(Wearpos.Torso, objs.bandos_chestplate, 1),
                TestArgs(Wearpos.LeftHand, objs.dragon_defender, 1),
                TestArgs(Wearpos.Legs, objs.bandos_tassets, 1),
                TestArgs(Wearpos.Hands, objs.barrows_gloves, 1),
                TestArgs(Wearpos.Feet, objs.dragon_boots, 1),
                TestArgs(Wearpos.Ring, objs.berserker_ring, 1),
                TestArgs(Wearpos.Quiver, objs.rune_arrow, 500),
            )
    }
}
