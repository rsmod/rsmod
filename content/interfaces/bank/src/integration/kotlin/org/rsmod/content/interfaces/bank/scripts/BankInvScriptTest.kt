package org.rsmod.content.interfaces.bank.scripts

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.rsmod.api.config.refs.params
import org.rsmod.api.testing.GameTestState
import org.rsmod.content.interfaces.bank.BankTab
import org.rsmod.game.inv.InvObj
import org.rsmod.game.type.util.UncheckedType

/* Obj transaction system is not thread-safe. */
@Execution(ExecutionMode.SAME_THREAD)
@OptIn(UncheckedType::class)
/*
 * It is important to note that the bank inv type automatically uncertifies banknotes, so you may
 * run into unexpected bank inv results after certain transactions if you are not aware of the
 * objs being used as input.
 */
class BankInvScriptTest {
    @Test
    fun GameTestState.`withdraw single obj`() =
        runGameTest(BankInvScript::class) {
            val bank = openBank(player)
            bank[0] = InvObj(1, 1)
            bank[1] = InvObj(2, 1)
            bank[3] = InvObj(3, 5)
            bank[4] = InvObj(4, 1)
            player.bankTabSizeMain = 5

            player.ifButton(bank_components.main_inventory, comsub = 3, op = MAIN_INV_WITHDRAW_1)
            advance()

            assertEquals(InvObj(3, 1), player.inv[0])
            assertEquals(InvObj(3, 4), bank[3])

            // Withdrawing from first slot.
            player.ifButton(bank_components.main_inventory, comsub = 0, op = MAIN_INV_WITHDRAW_1)
            advance()

            // Rest of objs should shift to occupy the empty slot.
            assertEquals(InvObj(1, 1), player.inv[1])
            assertEquals(InvObj(2, 1), bank[0])
            assertNull(bank[1])
            assertEquals(InvObj(3, 4), bank[2])
            assertEquals(InvObj(4, 1), bank[3])
        }

    @Test
    fun GameTestState.`withdraw obj with MAX_VALUE count`() =
        runGameTest(BankInvScript::class) {
            val stackable = objTypes.values.firstOrNull { it.isStackable }
            checkNotNull(stackable) { "Could not find an ObjType with `isStackable` flag." }

            val nonStackable = objTypes.values.firstOrNull { !it.isStackable }
            checkNotNull(nonStackable) { "Could not find an ObjType without `isStackable` flag." }

            val bank = openBank(player)
            bank[0] = InvObj(1, 1)
            bank[1] = InvObj(2, 1)
            bank[3] = InvObj(nonStackable, 100)
            bank[4] = InvObj(stackable, 100)
            player.bankTabSizeMain = 5

            // Withdraw stackable obj.
            player.ifButton(bank_components.main_inventory, comsub = 4, op = MAIN_INV_WITHDRAW_X)
            advance()

            player.resumeCountDialog(Int.MAX_VALUE)
            advance()

            assertEquals(InvObj(stackable, 100), player.inv[0])
            assertNull(bank[4])

            // Withdraw non-stackable obj.
            val expectedWithdraw = player.inv.freeSpace()
            player.ifButton(bank_components.main_inventory, comsub = 3, op = MAIN_INV_WITHDRAW_X)
            advance()

            player.resumeCountDialog(Int.MAX_VALUE)
            advance()

            assertEquals(expectedWithdraw, player.inv.count(nonStackable))
            assertTrue(player.inv.isFull())
            assertEquals(InvObj(nonStackable, 100 - expectedWithdraw), bank[3])

            assertEquals(InvObj(stackable, 100), player.inv[0])
            assertNull(bank[4])
        }

    @Test
    fun GameTestState.`withdraw un-certifiable obj as cert`() =
        runGameTest(BankInvScript::class) {
            val noCert = firstObjType { it.certlink == 0 }
            val bank = openBank(player)
            bank[0] = InvObj(noCert, 100)
            player.bankTabSizeMain = 1
            player.withdrawCert = true

            player.clearInv()
            player.ifButton(bank_components.main_inventory, comsub = 0, op = MAIN_INV_WITHDRAW_5)
            advance()

            assertEquals(InvObj(noCert, 1), player.inv[0])
            assertEquals(InvObj(noCert, 1), player.inv[1])
            assertEquals(InvObj(noCert, 1), player.inv[2])
            assertEquals(InvObj(noCert, 1), player.inv[3])
            assertEquals(InvObj(noCert, 1), player.inv[4])
            assertEquals(InvObj(noCert, 95), bank[0])
        }

    @Test
    fun GameTestState.`withdraw over non-stackable capacity`() =
        runGameTest(BankInvScript::class) {
            val noStack = firstObjType { !it.isStackable }
            val bank = openBank(player)
            bank[0] = InvObj(noStack, 100)
            player.bankTabSizeMain = 1

            player.clearInv()
            player.ifButton(bank_components.main_inventory, comsub = 0, op = MAIN_INV_WITHDRAW_ALL)
            advance()

            val expectedInv = player.inv.indices.map { InvObj(noStack, 1) }
            assertEquals(expectedInv, player.inv.toList())
            assertEquals(InvObj(noStack, 100 - 28), bank[0])
        }

    @Test
    fun GameTestState.`withdraw over stackable capacity`() =
        runGameTest(BankInvScript::class) {
            val stackable = firstObjType { it.isStackable }
            val bank = openBank(player)
            bank[0] = InvObj(stackable, Int.MAX_VALUE)
            player.bankTabSizeMain = 1

            player.clearInv()
            player.inv[0] = InvObj(stackable, 1000)

            player.ifButton(bank_components.main_inventory, comsub = 0, op = MAIN_INV_WITHDRAW_ALL)
            advance()

            assertEquals(InvObj(stackable, Int.MAX_VALUE), player.inv[0])
            assertEquals(InvObj(stackable, 1000), bank[0])
        }

    @Test
    fun GameTestState.`deposit single obj`() =
        runGameTest(BankInvScript::class) {
            val bank = openBank(player)
            player.inv[0] = InvObj(1, 1)
            player.inv[3] = InvObj(2, 100)
            player.inv[4] = InvObj(3, 1)
            player.inv[10] = InvObj(4, 1)
            player.inv[27] = InvObj(5, 1)

            check(bank.isEmpty())

            player.ifButton(bank_components.side_inventory, comsub = 3, op = SIDE_INV_DEPOSIT_1)
            advance()

            assertEquals(InvObj(2, 1), bank[0])
            assertEquals(InvObj(2, 99), player.inv[3])
            assertEquals(1, bank.occupiedSpace())
            assertEquals(5, player.inv.occupiedSpace())
        }

    @Test
    fun GameTestState.`deposit obj with MAX_VALUE count`() =
        runGameTest(BankInvScript::class) {
            val bank = openBank(player)
            player.inv[0] = InvObj(1, 1)
            player.inv[3] = InvObj(2, 100)
            player.inv[4] = InvObj(3, 1)
            player.inv[10] = InvObj(4, 1)
            player.inv[27] = InvObj(5, 1)

            check(bank.isEmpty())

            player.ifButton(bank_components.side_inventory, comsub = 3, op = SIDE_INV_DEPOSIT_X)
            advance()

            player.resumeCountDialog(Int.MAX_VALUE)
            advance()

            assertNull(player.inv[3])
            assertEquals(InvObj(2, 100), bank[0])
            assertEquals(1, bank.occupiedSpace())
            assertEquals(4, player.inv.occupiedSpace())
        }

    @Test
    fun GameTestState.`deposit full inv`() =
        runGameTest(BankInvScript::class) {
            val bank = openBank(player)
            player.inv[0] = InvObj(1, 1)
            player.inv[3] = InvObj(2, 100)
            player.inv[4] = InvObj(3, 1)
            player.inv[10] = InvObj(4, 1)
            player.inv[27] = InvObj(5, 1)

            check(bank.isEmpty())

            player.ifButton(bank_components.deposit_inventory)
            advance()

            assertEquals(5, bank.occupiedSpace())
            assertEquals(0, player.inv.occupiedSpace())
        }

    @Test
    fun GameTestState.`deposit full worn`() =
        runGameTest(BankInvScript::class) {
            val bank = openBank(player)
            player.worn[3] = InvObj(1, 1)
            player.worn[12] = InvObj(2, 100)
            player.worn[1] = InvObj(3, 1)

            check(bank.isEmpty())

            player.ifButton(bank_components.deposit_worn)
            advance()

            assertEquals(3, bank.occupiedSpace())
            assertEquals(0, player.worn.occupiedSpace())
        }

    @Test
    fun GameTestState.`deposit single unbankable obj`() =
        runGameTest(BankInvScript::class) {
            val unbankable = objTypes.values.firstOrNull { it.param(params.no_bank) != 0 }
            checkNotNull(unbankable) { "Could not find an ObjType with `no_bank` param." }

            val bank = openBank(player)
            player.inv[3] = InvObj(unbankable, 1)

            check(bank.isEmpty())

            player.ifButton(bank_components.side_inventory, comsub = 3, op = SIDE_INV_DEPOSIT_1)
            advance()

            assertEquals(InvObj(unbankable, 1), player.inv[3])
            assertEquals(0, bank.occupiedSpace())
            assertEquals(1, player.inv.occupiedSpace())
        }

    @Test
    fun GameTestState.`deposit full inv with unbankable obj`() =
        runGameTest(BankInvScript::class) {
            val unbankable = objTypes.values.firstOrNull { it.param(params.no_bank) != 0 }
            checkNotNull(unbankable) { "Could not find an ObjType with `no_bank` param." }

            val bank = openBank(player)
            player.inv[0] = InvObj(1, 1)
            player.inv[3] = InvObj(unbankable, 1)
            player.inv[4] = InvObj(3, 1)
            player.inv[10] = InvObj(4, 1)
            player.inv[27] = InvObj(5, 1)

            check(bank.isEmpty())

            player.ifButton(bank_components.deposit_inventory)
            advance()

            assertEquals(4, bank.occupiedSpace())
            assertEquals(1, player.inv.occupiedSpace())
            assertEquals(InvObj(unbankable, 1), player.inv[3])
        }

    @Test
    fun GameTestState.`deposit into focused tab`() =
        runGameTest(BankInvScript::class) {
            val bank = openBank(player)
            player.inv[0] = InvObj(1, 1)
            player.inv[3] = InvObj(2, 100)
            player.inv[4] = InvObj(3, 1)
            player.inv[10] = InvObj(4, 1)
            player.inv[27] = InvObj(5, 1)

            check(bank.isEmpty())

            player.selectedTab = BankTab.Tab1
            player.ifButton(bank_components.side_inventory, comsub = 3, op = SIDE_INV_DEPOSIT_1)

            advance()
            assertEquals(InvObj(2, 99), player.inv[3])
            assertEquals(InvObj(2, 1), bank[0])
            assertEquals(1, bank.occupiedSpace())
            assertEquals(5, player.inv.occupiedSpace())
            assertEquals(1, BankTab.Tab1.occupiedSpace(player))

            player.selectedTab = BankTab.Main
            player.ifButton(bank_components.side_inventory, comsub = 0, op = SIDE_INV_DEPOSIT_1)

            advance()
            assertNull(player.inv[0])
            assertEquals(InvObj(1, 1), bank[1])
            assertEquals(2, bank.occupiedSpace())
            assertEquals(4, player.inv.occupiedSpace())
            assertEquals(1, BankTab.Main.occupiedSpace(player))
            assertEquals(1, BankTab.Tab1.occupiedSpace(player))

            player.ifButton(bank_components.side_inventory, comsub = 3, op = SIDE_INV_DEPOSIT_5)

            advance()
            assertEquals(InvObj(2, 94), player.inv[3])
            assertEquals(InvObj(2, 6), bank[0])
            assertEquals(2, bank.occupiedSpace())
            assertEquals(4, player.inv.occupiedSpace())
            assertEquals(1, BankTab.Tab1.occupiedSpace(player))

            player.selectedTab = BankTab.Tab1
            player.ifButton(bank_components.side_inventory, comsub = 4, op = SIDE_INV_DEPOSIT_1)

            advance()
            assertNull(player.inv[4])
            assertEquals(InvObj(2, 6), bank[0]) // Initial obj deposited in tab 1
            assertEquals(InvObj(3, 1), bank[1]) // Obj deposited in main tab
            assertEquals(InvObj(1, 1), bank[2]) // New obj deposited in tab 1
            assertEquals(3, bank.occupiedSpace())
            assertEquals(3, player.inv.occupiedSpace())
            assertEquals(2, BankTab.Tab1.occupiedSpace(player))
            assertEquals(1, BankTab.Main.occupiedSpace(player))
        }

    @Test
    fun GameTestState.`drag side inv`() =
        runGameTest(BankInvScript::class) {
            openBank(player)

            player.inv[0] = InvObj(1, 1)
            player.inv[3] = InvObj(2, 5)
            player.inv[6] = InvObj(3, 1)

            player.ifButtonD(bank_components.side_inventory, fromComsub = 0, intoComsub = 3)

            advance()
            assertEquals(InvObj(2, 5), player.inv[0])
            assertEquals(InvObj(1, 1), player.inv[3])
            assertEquals(InvObj(3, 1), player.inv[6])
            assertEquals(3, player.inv.occupiedSpace())

            player.ifButtonD(bank_components.side_inventory, fromComsub = 3, intoComsub = 10)

            advance()
            assertEquals(InvObj(2, 5), player.inv[0])
            assertEquals(InvObj(1, 1), player.inv[10])
            assertEquals(InvObj(3, 1), player.inv[6])
            assertNull(player.inv[3])
            assertEquals(3, player.inv.occupiedSpace())
        }

    @Test
    fun GameTestState.`drag main inv`() =
        runGameTest(BankInvScript::class) {
            val bank = openBank(player)
            check(bank.isEmpty())

            val tab1 =
                listOf(
                    InvObj(1, 1),
                    InvObj(2, 1),
                    InvObj(3, 1),
                    null,
                    InvObj(4, 1),
                    null,
                    InvObj(5, 1),
                )

            val tab2 = listOf(InvObj(6, 1), InvObj(7, 1), InvObj(8, 1), InvObj(9, 1), InvObj(10, 1))

            val tab3 = listOf(InvObj(11, 1), null, null, null, InvObj(12, 1), null, InvObj(13, 1))

            val combined = (tab1 + tab2 + tab3).toTypedArray()
            combined.copyInto(bank.objs)
            player.bankTabSize1 = tab1.size
            player.bankTabSize2 = tab2.size
            player.bankTabSize3 = tab3.size

            // Insert from slot 7 to slot 11 in tab 2.
            player.insertMode = true
            player.ifButtonD(bank_components.main_inventory, fromComsub = 7, intoComsub = 11)

            advance()
            assertEquals(InvObj(7, 1), bank[7])
            assertEquals(InvObj(8, 1), bank[8])
            assertEquals(InvObj(9, 1), bank[9])
            assertEquals(InvObj(10, 1), bank[10])
            assertEquals(InvObj(6, 1), bank[11])
            assertEquals(tab2.size, player.bankTabSize2)

            // Insert from slot 16 to slot 18 in tab 3.
            player.insertMode = true
            player.ifButtonD(bank_components.main_inventory, fromComsub = 16, intoComsub = 18)

            advance()
            assertEquals(InvObj(11, 1), bank[12])
            assertNull(bank[13])
            assertNull(bank[14])
            assertNull(bank[15])
            assertNull(bank[16])
            assertEquals(InvObj(13, 1), bank[17])
            assertEquals(InvObj(12, 1), bank[18])
            assertEquals(tab3.size, player.bankTabSize3)

            // Swap slot 0 obj with occupied slot 2.
            player.insertMode = false
            player.ifButtonD(bank_components.main_inventory, fromComsub = 0, intoComsub = 2)

            advance()
            assertEquals(InvObj(3, 1), bank[0])
            assertEquals(InvObj(1, 1), bank[2])

            // Swap slot 0 with empty slot 3. This should cause all items to shift to the left.
            player.insertMode = false
            player.ifButtonD(bank_components.main_inventory, fromComsub = 0, intoComsub = 3)

            advance()
            assertEquals(InvObj(2, 1), bank[0])
            assertEquals(InvObj(1, 1), bank[1])
            assertEquals(InvObj(3, 1), bank[2])
            assertEquals(InvObj(4, 1), bank[3])
            assertNull(bank[4])
            assertEquals(InvObj(5, 1), bank[5])
            assertEquals(tab1.size - 1, player.bankTabSize1)
        }

    @Test
    fun GameTestState.`drag between tab invs`() =
        runGameTest(BankInvScript::class) {
            val bank = openBank(player)
            check(bank.isEmpty())

            val tab1 =
                listOf(
                    InvObj(1, 1),
                    InvObj(2, 1),
                    InvObj(3, 1),
                    null,
                    InvObj(4, 1),
                    null,
                    InvObj(5, 1),
                )

            val tab2 = listOf(InvObj(6, 1), InvObj(7, 1), InvObj(8, 1), InvObj(9, 1), InvObj(10, 1))

            val tab3 = listOf(InvObj(11, 1), null, null, null, InvObj(12, 1), null, InvObj(13, 1))

            val combined = (tab1 + tab2 + tab3).toTypedArray()
            combined.copyInto(bank.objs)
            player.bankTabSize1 = tab1.size
            player.bankTabSize2 = tab2.size
            player.bankTabSize3 = tab3.size

            player.insertMode = false
            player.ifButtonD(bank_components.main_inventory, fromComsub = 9, intoComsub = 0)

            advance()
            assertEquals(InvObj(8, 1), bank[0])
            assertEquals(InvObj(2, 1), bank[1])
            assertEquals(InvObj(3, 1), bank[2])
            assertNull(bank[3])
            assertEquals(InvObj(4, 1), bank[4])
            assertNull(bank[5])
            assertEquals(InvObj(5, 1), bank[6])
            assertEquals(InvObj(6, 1), bank[7])
            assertEquals(InvObj(7, 1), bank[8])
            assertEquals(InvObj(1, 1), bank[9])
            assertEquals(tab1.size, player.bankTabSize1)
            assertEquals(tab2.size, player.bankTabSize2)

            // Swap back to initial state.
            player.insertMode = false
            player.ifButtonD(bank_components.main_inventory, fromComsub = 9, intoComsub = 0)

            advance()
            assertEquals(InvObj(1, 1), bank[0])
            assertEquals(InvObj(2, 1), bank[1])
            assertEquals(InvObj(3, 1), bank[2])
            assertNull(bank[3])
            assertEquals(InvObj(4, 1), bank[4])
            assertNull(bank[5])
            assertEquals(InvObj(5, 1), bank[6])
            assertEquals(InvObj(6, 1), bank[7])
            assertEquals(InvObj(7, 1), bank[8])
            assertEquals(InvObj(8, 1), bank[9])
            assertEquals(tab1.size, player.bankTabSize1)
            assertEquals(tab2.size, player.bankTabSize2)

            // Insert from tab 2 into tab 1.
            player.insertMode = true
            player.ifButtonD(bank_components.main_inventory, fromComsub = 9, intoComsub = 0)

            advance()
            assertEquals(InvObj(8, 1), bank[0])
            assertEquals(InvObj(1, 1), bank[1])
            assertEquals(InvObj(2, 1), bank[2])
            assertEquals(InvObj(3, 1), bank[3])
            assertNull(bank[4])
            assertEquals(InvObj(4, 1), bank[5])
            assertNull(bank[6])
            assertEquals(InvObj(5, 1), bank[7])
            assertEquals(InvObj(6, 1), bank[8])
            assertEquals(InvObj(7, 1), bank[9])
            assertEquals(InvObj(9, 1), bank[10])
            assertEquals(tab1.size + 1, player.bankTabSize1)
            assertEquals(tab2.size - 1, player.bankTabSize2)
        }

    @Test
    fun GameTestState.`drag from tab inv to tab upper component`() =
        runGameTest(BankInvScript::class) {
            val bank = openBank(player)
            check(bank.isEmpty())

            val tab1 =
                listOf(
                    InvObj(1, 1),
                    InvObj(2, 1),
                    InvObj(3, 1),
                    null,
                    InvObj(4, 1),
                    null,
                    InvObj(5, 1),
                )

            val tab2 = listOf(InvObj(6, 1), InvObj(7, 1), InvObj(8, 1), InvObj(9, 1), InvObj(10, 1))

            val tab3 = listOf(InvObj(11, 1), null, null, null, InvObj(12, 1), null, InvObj(13, 1))

            val combined = (tab1 + tab2 + tab3).toTypedArray()
            combined.copyInto(bank.objs)
            player.bankTabSize1 = tab1.size
            player.bankTabSize2 = tab2.size
            player.bankTabSize3 = tab3.size

            val tab1Comsub = bank_comsubs.other_tabs.first

            // Even with swap mode enabled, dragging into a new tab acts as a shift/insert.
            player.insertMode = false
            player.ifButtonD(
                fromComponent = bank_components.main_inventory,
                fromComsub = 9,
                fromObj = objTypes[bank[9]?.id],
                intoComponent = bank_components.tabs,
                intoComsub = tab1Comsub + 2,
                intoObj = null,
            )

            advance()
            assertEquals(InvObj(9, 1), bank[9])
            assertEquals(InvObj(10, 1), bank[10])
            assertEquals(InvObj(11, 1), bank[11])
            assertEquals(InvObj(8, 1), bank[12])
            assertNull(bank[13])
            assertNull(bank[14])
            assertEquals(InvObj(12, 1), bank[15])
            assertNull(bank[16])
            assertEquals(InvObj(13, 1), bank[17])
            assertEquals(tab2.size - 1, player.bankTabSize2)
            // Obj inserted into tab took a previously empty slot - size does not increase.
            assertEquals(tab3.size, player.bankTabSize3)
        }

    @Test
    fun GameTestState.`drag bank tabs`() =
        runGameTest(BankInvScript::class) {
            val bank = openBank(player)
            check(bank.isEmpty())

            val tab1 =
                listOf(
                    InvObj(1, 1),
                    InvObj(2, 1),
                    InvObj(3, 1),
                    null,
                    InvObj(4, 1),
                    null,
                    InvObj(5, 1),
                )

            val tab2 = listOf(InvObj(6, 1), InvObj(7, 1), InvObj(8, 1), InvObj(9, 1), InvObj(10, 1))

            val tab3 = listOf(InvObj(11, 1), null, null, null, InvObj(12, 1), null, InvObj(13, 1))

            val combined = (tab1 + tab2 + tab3).toTypedArray()
            combined.copyInto(bank.objs)
            player.bankTabSize1 = tab1.size
            player.bankTabSize2 = tab2.size
            player.bankTabSize3 = tab3.size

            val tab1Comsub = bank_comsubs.other_tabs.first
            val initialSize = bank.occupiedSpace()

            fun assertTabSizes(tabSize1: Int, tabSize2: Int, tabSize3: Int) {
                assertEquals(player.bankTabSize1, tabSize1)
                assertEquals(player.bankTabSize2, tabSize2)
                assertEquals(player.bankTabSize3, tabSize3)
                assertEquals(initialSize, bank.occupiedSpace())
            }

            // Shift from 1st tab index to 3rd tab index.
            player.ifButtonD(bank_components.tabs, tab1Comsub, tab1Comsub + 2)

            advance()
            assertEquals(tab2, bank.objs.copyOfRange(0, tab2.size).toList())
            assertEquals(tab3, bank.objs.copyOfRange(tab2.size, tab2.size + tab3.size).toList())
            assertEquals(tab1, bank.objs.copyOfRange(tab2.size + tab3.size, combined.size).toList())
            assertTabSizes(tab2.size, tab3.size, tab1.size)

            // Shift from 1st tab index to 3rd tab index.
            player.ifButtonD(bank_components.tabs, tab1Comsub, tab1Comsub + 2)

            advance()
            assertEquals(tab3, bank.objs.copyOfRange(0, tab3.size).toList())
            assertEquals(tab1, bank.objs.copyOfRange(tab3.size, tab3.size + tab1.size).toList())
            assertEquals(tab2, bank.objs.copyOfRange(tab3.size + tab1.size, combined.size).toList())
            assertTabSizes(tab3.size, tab1.size, tab2.size)

            // Shift from 2nd tab index to 1st tab index.
            player.ifButtonD(bank_components.tabs, tab1Comsub + 1, tab1Comsub)

            advance()
            assertEquals(tab1, bank.objs.copyOfRange(0, tab1.size).toList())
            assertEquals(tab3, bank.objs.copyOfRange(tab1.size, tab1.size + tab3.size).toList())
            assertEquals(tab2, bank.objs.copyOfRange(tab1.size + tab3.size, combined.size).toList())
            assertTabSizes(tab1.size, tab3.size, tab2.size)

            // Shift from 2nd tab index to 3rd tab index.
            player.ifButtonD(bank_components.tabs, tab1Comsub + 1, tab1Comsub + 2)

            advance()
            assertEquals(tab1, bank.objs.copyOfRange(0, tab1.size).toList())
            assertEquals(tab2, bank.objs.copyOfRange(tab1.size, tab1.size + tab2.size).toList())
            assertEquals(tab3, bank.objs.copyOfRange(tab1.size + tab2.size, combined.size).toList())
            assertTabSizes(tab1.size, tab2.size, tab3.size)
        }

    /*
     * It is important for bank to compress on if_close as there are certain mechanics that can
     * deposit objs into banks without the player opening it. If it did not compress on close, these
     * objs would take up "empty" slots instead of new slots. (We know this is how it is handled in
     * the official game)
     */
    @Test
    fun GameTestState.`compress bank on close`() =
        runGameTest(BankInvScript::class, BankOpenScript::class) {
            val bank = openBank(player)
            check(bank.isEmpty())

            bank[0] = InvObj(1, 1)
            bank[1] = null
            bank[2] = InvObj(3, 1)
            bank[3] = null
            bank[4] = InvObj(5, 1)
            bank[5] = InvObj(6, 1)
            bank[6] = null
            bank[7] = InvObj(8, 1)
            bank[8] = null
            bank[9] = null
            bank[10] = InvObj(11, 1)

            player.bankTabSize1 = 5
            player.bankTabSize2 = 5
            player.bankTabSizeMain = 1

            player.ifClose()

            advance()
            assertEquals(6, bank.lastOccupiedSlot())
            assertEquals(InvObj(1, 1), bank[0])
            assertEquals(InvObj(3, 1), bank[1])
            assertEquals(InvObj(5, 1), bank[2])
            assertEquals(InvObj(6, 1), bank[3])
            assertEquals(InvObj(8, 1), bank[4])
            assertEquals(InvObj(11, 1), bank[5])
            assertEquals(3, player.bankTabSize1)
            assertEquals(2, player.bankTabSize2)
            assertEquals(1, player.bankTabSizeMain)
        }
}
