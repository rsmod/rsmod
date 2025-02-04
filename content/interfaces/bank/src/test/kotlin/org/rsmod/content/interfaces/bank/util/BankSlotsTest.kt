package org.rsmod.content.interfaces.bank.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.rsmod.api.testing.params.TestArgs
import org.rsmod.api.testing.params.TestArgsProvider
import org.rsmod.api.testing.params.TestWithArgs
import org.rsmod.content.interfaces.bank.util.BankSlots.shiftLeadingGapsToTail
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType
import org.rsmod.game.type.inv.InvTypeBuilder
import org.rsmod.game.type.util.UncheckedType

class BankSlotsTest {
    @Test
    fun `shift leading gaps starting from middle-range index`() {
        val input = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 16, 17)
        val expected = listOf(0, 1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 16, 17, null)
        val bank = createBank().withInput(input)

        bank[8] = null
        val result = shiftLeadingGapsToTail(bank, 8 until input.size)
        val output = bank.objs.copyOfRange(0, input.size).map { it?.id }

        assertEquals(expected, output)
        assertEquals(1, result.leadingGaps)
        assertEquals(1, result.trailingGaps)
        assertEquals(input.size - 1, result.compactSize)
    }

    @Test
    fun `shift should return early when all slots are null`() {
        val input = listOf(null, null, null, null, null)
        val bank = createBank().withInput(input)

        val result = shiftLeadingGapsToTail(bank, input.indices)

        assertEquals(5, result.leadingGaps)
        assertEquals(0, result.compactSize)
        assertEquals(5, result.trailingGaps)
        assertFalse(bank.hasModifiedSlots())
    }

    @Test
    fun `shift should return early when there are no nulls`() {
        val input = listOf(0, 1, 2, 3, 4)
        val bank = createBank().withInput(input)

        val result = shiftLeadingGapsToTail(bank, input.indices)

        assertEquals(0, result.leadingGaps)
        assertEquals(0, result.compactSize)
        assertEquals(0, result.trailingGaps)
        assertFalse(bank.hasModifiedSlots())
    }

    @Test
    fun `shift should return early when first null is at last slot`() {
        val input = listOf(0, 1, 2, 3, null)
        val bank = createBank().withInput(input)

        val result = shiftLeadingGapsToTail(bank, input.indices)

        assertEquals(0, result.leadingGaps)
        assertEquals(4, result.compactSize)
        assertEquals(1, result.trailingGaps)
    }

    @TestWithArgs(GapRemovalProvider::class)
    fun `shift leading gaps to tail of array`(input: List<Int?>, expected: List<Int?>) {
        require(input.size == expected.size) {
            "Input size must match expected size: input=$input, expected=$expected"
        }

        val bank = createBank().withInput(input)
        val result = shiftLeadingGapsToTail(bank, input.indices)

        val output = bank.map { it?.id }.subList(0, expected.size)
        assertEquals(expected, output)

        val expectedLeading = input.takeWhile { it == null }.count()
        assertEquals(expectedLeading, result.leadingGaps)

        val expectedCompactSize = expected.indexOfLast { it != null } + 1
        assertEquals(expectedCompactSize, result.compactSize)

        val expectedTrailing = expected.reversed().takeWhile { it == null }.count()
        assertEquals(expectedTrailing, result.trailingGaps)
    }

    private object GapRemovalProvider : TestArgsProvider {
        override fun args(): List<TestArgs> {
            return listOf(
                TestArgs(
                    listOf(null, null, 0, null, null, 1, 2, null),
                    listOf(0, null, null, 1, 2, null, null, null),
                ),
                TestArgs(
                    listOf(null, 0, null, 1, 2, null, null, null, null),
                    listOf(0, null, 1, 2, null, null, null, null, null),
                ),
                TestArgs(
                    listOf(0, 1, 2, null, null, null, null),
                    listOf(0, 1, 2, null, null, null, null),
                ),
                TestArgs(
                    listOf(0, 1, null, 2, null, null, null, null),
                    listOf(0, 1, null, 2, null, null, null, null),
                ),
                TestArgs(
                    listOf(null, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
                    listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, null),
                ),
                TestArgs(
                    listOf(0, null, null, null, null, null, null, null, null),
                    listOf(0, null, null, null, null, null, null, null, null),
                ),
                TestArgs(
                    listOf(null, null, null, null, null, null, null, null, 0),
                    listOf(0, null, null, null, null, null, null, null, null),
                ),
                TestArgs(
                    listOf(null, null, null, null, null, null, 0, null, 1),
                    listOf(0, null, 1, null, null, null, null, null, null),
                ),
            )
        }
    }

    private fun createBank(): Inventory {
        val builder =
            InvTypeBuilder("test_bank").apply {
                this.size = 50
                this.scope = InvScope.Perm
                this.stack = InvStackType.Always
                this.protect = false
                this.placeholders = true
            }
        return Inventory.create(builder.build(-1))
    }

    private fun Inventory.withInput(input: List<Int?>): Inventory {
        for (i in input.indices) {
            this[i] = toObj(input[i])
        }
        clearModifiedSlots()
        return this
    }
}

@OptIn(UncheckedType::class) private fun toObj(id: Int?): InvObj? = id?.let { InvObj(it, 0) }
