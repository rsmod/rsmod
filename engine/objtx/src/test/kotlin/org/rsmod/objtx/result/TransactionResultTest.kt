package org.rsmod.objtx.result

import java.util.stream.Stream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.rsmod.objtx.Inventory
import org.rsmod.objtx.Obj
import org.rsmod.objtx.TransactionInventory
import org.rsmod.objtx.abyssal_whip
import org.rsmod.objtx.inv
import org.rsmod.objtx.red_partyhat
import org.rsmod.objtx.select
import org.rsmod.objtx.transaction

@Execution(ExecutionMode.SAME_THREAD)
class TransactionResultTest {
    @Test
    fun `lenient complete success`() {
        val inventory = inv()
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
                count = inventory.size
            }
        }
        assertTrue(transaction.success)
        assertFalse(transaction.failure)
        assertNotNull(transaction[0])
        val result = checkNotNull(transaction[0])
        assertEquals(inventory.size, result.completed)
        assertEquals(0, result.left)
        assertTrue(result.fullSuccess)
        assertFalse(result.emptySuccess)
        assertFalse(result.partialSuccess)
    }

    @Test
    fun `lenient partial success`() {
        val inventory = inv()
        val left = 5
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
                count = inventory.size + left
            }
        }
        assertTrue(transaction.success)
        assertFalse(transaction.failure)
        assertNotNull(transaction[0])
        val result = checkNotNull(transaction[0])
        assertEquals(inventory.size, result.completed)
        assertEquals(left, result.left)
        assertTrue(result.partialSuccess)
        assertFalse(result.emptySuccess)
        assertFalse(result.fullSuccess)
    }

    @Test
    fun `lenient empty success`() {
        val inventory = inv()
        for (i in inventory.indices) {
            inventory[i] = Obj(abyssal_whip)
        }
        val request = 20
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
                count = request
            }
        }
        assertTrue(transaction.success)
        assertFalse(transaction.failure)
        assertNotNull(transaction[0])
        val result = checkNotNull(transaction[0])
        assertEquals(0, result.completed)
        assertEquals(request, result.left)
        assertTrue(result.emptySuccess)
        assertFalse(result.fullSuccess)
        assertFalse(result.partialSuccess)
    }

    @ParameterizedTest
    @ArgumentsSource(InvPartialRangeProvider::class)
    fun `partial success when completed count is in range 1 until requested`(
        request: Int,
        occupied: Int,
    ) {
        val inventory = inv()
        for (occupy in 0 until occupied) {
            inventory[occupy] = Obj(red_partyhat)
        }
        val transaction = transaction {
            val inv = select(inventory)
            insert {
                into = inv
                obj = abyssal_whip
                count = request
            }
        }
        assertTrue(transaction.success)
        assertFalse(transaction.failure)
        assertNotNull(transaction[0])
        val result = checkNotNull(transaction[0])
        assertEquals(request - occupied, result.completed)
        assertEquals(occupied, result.left)
        assertTrue(result.partialSuccess)
        assertFalse(result.emptySuccess)
        assertFalse(result.fullSuccess)
    }

    private object InvPartialRangeProvider : ArgumentsProvider {
        private val dummyInv = Inventory(TransactionInventory.NormalStack, arrayOfNulls(28))

        override fun provideArguments(context: ExtensionContext): Stream<out Arguments> {
            val args = (1 until dummyInv.size).map { Arguments.of(dummyInv.size, it) }
            return Stream.of(*args.toTypedArray())
        }
    }
}
