package org.rsmod.content.interfaces.bank.scripts

import jakarta.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import org.rsmod.api.combat.player.WeaponSpeeds
import org.rsmod.api.combat.player.WornBonuses
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.content
import org.rsmod.api.config.refs.invs
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.synths
import org.rsmod.api.invtx.invCompress
import org.rsmod.api.invtx.invTransaction
import org.rsmod.api.invtx.select
import org.rsmod.api.player.events.interact.HeldBanksideEvents
import org.rsmod.api.player.events.interact.HeldDropEvents
import org.rsmod.api.player.output.ClientScripts.mesLayerClose
import org.rsmod.api.player.output.UpdateInventory.resendSlot
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.player.ui.IfModalDrag
import org.rsmod.api.player.worn.HeldEquipResult
import org.rsmod.api.player.worn.WornUnequipOp
import org.rsmod.api.player.worn.WornUnequipResult
import org.rsmod.api.script.onIfModalButton
import org.rsmod.api.script.onIfModalDrag
import org.rsmod.api.script.onPlayerLogIn
import org.rsmod.api.script.onPlayerQueue
import org.rsmod.content.interfaces.bank.BankTab
import org.rsmod.content.interfaces.bank.QuantityMode
import org.rsmod.content.interfaces.bank.alwaysPlacehold
import org.rsmod.content.interfaces.bank.bankCapacity
import org.rsmod.content.interfaces.bank.configs.bank_components
import org.rsmod.content.interfaces.bank.configs.bank_comsubs
import org.rsmod.content.interfaces.bank.configs.bank_constants
import org.rsmod.content.interfaces.bank.configs.bank_enums
import org.rsmod.content.interfaces.bank.configs.bank_objs
import org.rsmod.content.interfaces.bank.configs.bank_queues
import org.rsmod.content.interfaces.bank.insertMode
import org.rsmod.content.interfaces.bank.lastQtyInput
import org.rsmod.content.interfaces.bank.leftClickQtyMode
import org.rsmod.content.interfaces.bank.selectedTab
import org.rsmod.content.interfaces.bank.setBankWornBonuses
import org.rsmod.content.interfaces.bank.setBanksideExtraOps
import org.rsmod.content.interfaces.bank.util.BankSlots
import org.rsmod.content.interfaces.bank.util.bulkShift
import org.rsmod.content.interfaces.bank.util.leftShift
import org.rsmod.content.interfaces.bank.util.offset
import org.rsmod.content.interfaces.bank.util.rightShift
import org.rsmod.content.interfaces.bank.util.shiftInsert
import org.rsmod.content.interfaces.bank.withdrawCert
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.inv.Inventory
import org.rsmod.game.obj.InvObj
import org.rsmod.game.obj.isType
import org.rsmod.game.type.interf.IfButtonOp
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.UnpackedObjType
import org.rsmod.game.type.obj.Wearpos
import org.rsmod.game.type.obj.isType
import org.rsmod.game.type.util.EnumTypeMapResolver
import org.rsmod.objtx.TransactionResult
import org.rsmod.objtx.isOk
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

/*
 * Note: This implementation prioritizes correctness in emulating the expected bank quirks over raw
 * performance and "elegant" solutions. Most inventory transactions here could be optimized for
 * speed, but they remain structured this way to closely match the intended banking mechanics.
 *
 * While the performance trade-off is expected to be negligible under normal conditions, any future
 * changes or optimizations should be informed by benchmarking.
 *
 * If performance becomes a priority, a complete rewrite of this class with efficiency in mind would
 * likely be the best approach. This should be straightforward as long as strict accuracy to the
 * original system is not a requirement.
 */
class BankInvScript
@Inject
constructor(
    private val eventBus: EventBus,
    private val objTypes: ObjTypeList,
    private val wornBonuses: WornBonuses,
    private val weaponSpeeds: WeaponSpeeds,
    private val enumResolver: EnumTypeMapResolver,
) : PluginScript() {
    override fun ScriptContext.startUp() {
        onPlayerLogIn { player.setDefaultCapacity() }

        onIfModalButton(bank_components.side_inventory) { sideInvOp(it.comsub, it.op) }
        onIfModalButton(bank_components.main_inventory) { mainInvOp(it.comsub, it.op) }
        onIfModalButton(bank_components.worn_inventory) { wornInvOp(it.comsub, it.op) }
        onIfModalButton(bank_components.deposit_inventory) { depositInv() }
        onIfModalButton(bank_components.deposit_worn) { depositWorn() }
        onIfModalButton(bank_components.tabs) { selectTab(it.comsub, it.op) }
        onIfModalButton(bank_components.incinerator_confirm) { incinerate(it.comsub, it.obj) }
        onIfModalDrag(bank_components.tabs) { dragTab(it) }
        onIfModalDrag(bank_components.side_inventory) { dragSideInv(it) }
        onIfModalDrag(bank_components.main_inventory) { dragMainInv(it) }
        onIfModalDrag(bank_components.worn_inventory) { dragSideInv(it) }
        onIfModalDrag(bank_components.main_inventory, bank_components.tabs) { dragIntoTab(it) }

        val wornComponents = enumResolver[bank_enums.worn_component_map].filterValuesNotNull()
        for ((slot, component) in wornComponents) {
            onIfModalButton(component) { wornOp(slot, it.op) }
        }

        onPlayerQueue(bank_queues.bank_compress) { compressBank() }
    }

    private suspend fun ProtectedAccess.mainInvOp(slot: Int, op: IfButtonOp) {
        if (op == IfButtonOp.Op10) {
            objExamine(bank, slot)
            return
        }
        mainInvWithdraw(slot, op)
    }

    private suspend fun ProtectedAccess.mainInvWithdraw(slot: Int, op: IfButtonOp) {
        val obj = bank[slot] ?: return resendSlot(bank, 0)
        val objType = objTypes[obj]
        if (objType.isPlaceholder) {
            bank[slot] = null
            notifySlotUpdate(slot)
            return
        } else if (objType.isType(bank_objs.filler)) {
            if (op == IfButtonOp.Op7) {
                bank[slot] = null
                soundSynth(synths.paper_turn)
                notifySlotUpdate(slot)
            } else if (op == IfButtonOp.Op8) {
                removeFillers()
            }
            return
        }
        val count =
            when (op) {
                IfButtonOp.Op1 -> resolveLeftClickQty()
                IfButtonOp.Op2 -> 1
                IfButtonOp.Op3 -> 5
                IfButtonOp.Op4 -> 10
                IfButtonOp.Op5 -> resolveLastDepositQty()
                IfButtonOp.Op6 -> {
                    val input = countDialog()
                    lastQtyInput = input
                    input
                }
                IfButtonOp.Op7 -> obj.count
                IfButtonOp.Op8 -> obj.count - 1
                IfButtonOp.Op9 -> obj.count
                else -> throw IllegalStateException("Invalid main inv op: $op")
            }
        val transaction =
            invMoveFromSlot(
                from = bank,
                into = inv,
                fromSlot = slot,
                count = count,
                strict = false,
                cert = withdrawCert,
                placehold = alwaysPlacehold || op == IfButtonOp.Op9,
            )
        val result = transaction[0]

        if (result == TransactionResult.NotEnoughSpace) {
            mes("You don't have enough inventory space.")
            return
        }

        if (!result.isOk()) {
            return
        }

        if (withdrawCert && !objType.canCert) {
            mes("This item cannot be withdrawn as a note.")
        }

        if (!result.fullSuccess) {
            mes("You don't have enough inventory space to withdraw that many.")
        }

        if (bank[slot] == null) {
            notifySlotUpdate(slot)
        }

        setBanksideExtraOps()
    }

    private suspend fun ProtectedAccess.sideInvOp(slot: Int, op: IfButtonOp) {
        if (op == IfButtonOp.Op10) {
            objExamine(inv, slot)
            return
        }
        val count =
            when (op) {
                IfButtonOp.Op2 -> resolveLeftClickQty()
                IfButtonOp.Op3 -> 1
                IfButtonOp.Op4 -> 5
                IfButtonOp.Op5 -> 10
                IfButtonOp.Op6 -> resolveLastDepositQty()
                IfButtonOp.Op7 -> {
                    val input = countDialog()
                    lastQtyInput = input
                    input
                }
                IfButtonOp.Op8 -> Int.MAX_VALUE
                IfButtonOp.Op9 -> {
                    clickBanksideExtraOp(slot)
                    return
                }
                else -> throw IllegalStateException("Invalid main inv op: $op")
            }
        invDeposit(slot, count, inv)
    }

    private fun ProtectedAccess.invDeposit(slot: Int, count: Int, inventory: Inventory): Boolean {
        val obj = inventory[slot]
        if (obj == null) {
            resendSlot(inventory, 0)
            return false
        }

        val objType = objTypes[obj]

        if (objType.param(params.no_bank) != 0) {
            mes("A magical force prevents you from banking this item!")
            return false
        }

        val tab = selectedTab

        val placeholder = objTypes.placeholder(objType)
        val containedObjSlot = bank.indexOfFirst { it?.id == obj.id || it?.id == placeholder.id }
        val prioritySlot =
            if (containedObjSlot != -1) {
                containedObjSlot
            } else {
                tab.slotRange(this).firstOrNull { bank[it] == null }
            }

        // Mainly done for emulation purposes - compress all other bank tabs. This is not done
        // for main bank tab.
        if (prioritySlot == null && !tab.isMainTab) {
            val others = BankTab.entries - tab
            for (other in others) {
                val slots = other.slotRange(this)
                compressTabObjs(other)
                trimGapsAndReturnTrailingGaps(slots)
            }
        }

        val tabSlots = tab.slotRange(this)
        val insertQuery =
            player.invTransaction(inventory, bank) {
                val fromInv = select(inventory)
                val bankInv = select(bank)
                if (prioritySlot == null && !tab.isMainTab) {
                    rightShift {
                        this.from = bankInv
                        this.startSlot = tabSlots.last + 1
                        this.shiftCount = 1
                    }
                }
                transfer {
                    this.from = fromInv
                    this.into = bankInv
                    this.fromSlot = slot
                    this.intoSlot = prioritySlot ?: tabSlots.first
                    this.intoCapacity = bankCapacity
                    this.count = count
                    this.uncert = true
                    this.strict = false
                }
            }
        val result = insertQuery.results.last()

        // TODO(content): This message may be incorrect.
        if (result == TransactionResult.NotEnoughSpace && bank.occupiedSpace() >= bankCapacity) {
            mes("You don't have enough space in your bank account.")
            return false
        }

        if (result == TransactionResult.NotEnoughSpace) {
            mes("You already have a full stack of that item in the bank.")
            return false
        }

        if (!result.isOk()) {
            return false
        }

        // Cheap way of checking if obj has taken a new slot in bank.
        val expectedSlot = tabSlots.last + 1
        val expectedObj = objTypes.uncert(obj)
        if (bank[expectedSlot]?.id == expectedObj.id) {
            tab.increaseSize(this)
        }

        if (!result.fullSuccess) {
            mes("You already have a full stack of that item in the bank.")
        }

        setBanksideExtraOps()
        return true
    }

    private fun ProtectedAccess.wornInvOp(slot: Int, op: IfButtonOp) {
        if (op == IfButtonOp.Op10) {
            objExamine(inv, slot)
            return
        }

        val obj = inv[slot] ?: return resendSlot(inv, 0)
        if (op == IfButtonOp.Op9) {
            invDeposit(slot, obj.count, inv)
            return
        }

        if (op == IfButtonOp.Op1) {
            val type = objTypes[obj]
            if (type.wearpos1 != -1) {
                val result = invEquip(slot)
                if (result is HeldEquipResult.Fail) {
                    result.messages.forEach(::mes)
                    return
                }
                setBanksideExtraOps()
                setBankWornBonuses()
            }
            return
        }
    }

    private fun ProtectedAccess.depositInv() {
        if (inv.isEmpty()) {
            mes("You have nothing to deposit.")
            return
        }
        depositInventory(inv)
    }

    private fun ProtectedAccess.depositWorn() {
        if (worn.isEmpty()) {
            mes("You have nothing to deposit.")
            return
        }
        val startWearposObjs = Wearpos.entries.associateWith { worn[it.slot] }
        depositInventory(worn)

        for ((wearpos, oldObj) in startWearposObjs) {
            if (oldObj == null || oldObj == worn[wearpos.slot]) {
                continue
            }
            WornUnequipOp.notifyWornUnequip(player, wearpos, objTypes[oldObj], eventBus)
        }
    }

    private fun ProtectedAccess.depositInventory(from: Inventory) {
        val unbankableSlots =
            from.indices
                .filter {
                    val obj = from[it]
                    obj != null && objTypes[obj].param(params.no_bank) != 0
                }
                .toHashSet()

        val tab = selectedTab
        if (tab.isMainTab) {
            val startLastOccupiedSpace = bank.lastOccupiedSlot()
            val transaction =
                invMoveInv(
                    from = from,
                    into = bank,
                    untransform = true,
                    intoStartSlot = tab.firstSlot(this),
                    intoCapacity = bankCapacity,
                    keepSlots = unbankableSlots,
                )
            val result = transaction[0]

            if (result == TransactionResult.NotEnoughSpace) {
                mes("Your bank cannot hold your items.")
                return
            }

            if (transaction.completed() == 0 && unbankableSlots.isNotEmpty()) {
                mes("Your items cannot be stored in the bank.")
            } else if (unbankableSlots.isNotEmpty()) {
                mes("Some of your items cannot be stored in the bank.")
            }

            val lastOccupiedSlotDiff = bank.lastOccupiedSlot() - startLastOccupiedSpace
            if (lastOccupiedSlotDiff < 0) {
                throw IllegalStateException(
                    "`lastOccupiedSlotDiff` should not be negative: " +
                        "start=$startLastOccupiedSpace, curr=${bank.lastOccupiedSlot()}"
                )
            }

            if (lastOccupiedSlotDiff > 0) {
                tab.increaseSize(this, lastOccupiedSlotDiff)
            }

            setBanksideExtraOps()
            return
        }

        val containedBankObjs = bank.mapNotNull { it?.id }.toHashSet()

        val uniqueInvObjs = mutableSetOf<Int>()
        for (invObj in from) {
            val type = invObj?.let(objTypes::get) ?: continue
            val uncert = objTypes.uncert(type)
            if (uncert.param(params.no_bank) != 0) {
                continue
            }
            uniqueInvObjs += uncert.id
        }

        val containedObjMatches =
            uniqueInvObjs.count { obj ->
                val type = objTypes.untransform(objTypes.getValue(obj))
                val containsType = type.id in containedBankObjs
                val containsPlaceholder =
                    type.hasPlaceholder && objTypes.placeholder(type).id in containedBankObjs
                containsType || containsPlaceholder
            }
        val requiredSlots = uniqueInvObjs.count()
        val emptySlotCount = tab.slotRange(this).count { bank[it] == null }
        val newInsertSlots = requiredSlots - containedObjMatches - emptySlotCount

        check(newInsertSlots >= 0) {
            "`newInsertSlots` should not be negative: $newInsertSlots " +
                "(required=$requiredSlots, contained=$containedObjMatches, empty=$emptySlotCount)"
        }

        // Make space for new obj insertions by moving all objs that come _after_ the target bank
        // tab by the corresponding slot amount.
        if (newInsertSlots > 0) {

            // Compress all tabs aside from the target tab to make space.
            val others = BankTab.entries - tab
            for (other in others) {
                val slots = other.slotRange(this)
                compressTabObjs(other)
                trimGapsAndReturnTrailingGaps(slots)
            }

            val tabSlots = tab.slotRange(this)
            val shiftQuery =
                player.invTransaction(bank) {
                    val bankInv = select(bank)
                    rightShift {
                        this.from = bankInv
                        this.startSlot = tabSlots.last + 1
                        this.shiftCount = newInsertSlots
                    }
                }

            if (shiftQuery.failure) {
                mes("Your bank cannot hold your items.")
                return
            }
        }

        val fromSlots = from.indices.filter { from[it] != null }.distinctBy { from[it]?.id }
        val tabSlots = tab.slotRange(this)

        val bankCapacity = bankCapacity
        val filteredFromSlots = fromSlots - unbankableSlots
        val transferQuery =
            player.invTransaction(from, bank) {
                val fromInv = select(from)
                val bankInv = select(bank)
                for (slot in filteredFromSlots) {
                    transfer {
                        this.from = fromInv
                        this.into = bankInv
                        this.fromSlot = slot
                        this.intoSlot = tabSlots.first
                        this.intoCapacity = bankCapacity
                        this.count = Int.MAX_VALUE
                        this.uncert = true
                        this.untransform = true
                        this.strict = false
                    }
                }
            }
        val noneCompleted = transferQuery.noneCompleted()

        if (noneCompleted && filteredFromSlots.isEmpty()) {
            mes("Your items cannot be stored in the bank.")
            return
        }

        if (noneCompleted) {
            mes("Your bank cannot hold your items.")
            return
        }

        if (unbankableSlots.isNotEmpty()) {
            mes("Some of your items cannot be stored in the bank.")
        }

        if (newInsertSlots > 0) {
            tab.increaseSize(this, newInsertSlots)
        }
    }

    private fun ProtectedAccess.dragTab(drag: IfModalDrag) {
        val fromTabIndex = drag.selectedSlot ?: return resendSlot(bank, 0)
        val intoTabIndex = drag.targetSlot ?: return resendSlot(bank, 0)
        val fromTab = BankTab.forIndex(fromTabIndex - bank_comsubs.other_tabs.first)
        val intoTab = BankTab.forIndex(intoTabIndex - bank_comsubs.other_tabs.first)

        if (fromTab == null || fromTab.isMainTab || fromTab.isEmpty(this)) {
            resendSlot(bank, 0)
            return
        }

        if (intoTab == null || intoTab.isMainTab || intoTab.isEmpty(this)) {
            resendSlot(bank, 0)
            return
        }

        val fromSlots = fromTab.slotRange(this)
        val intoSlots = intoTab.slotRange(this)
        val intoSlot = if (fromTab.index > intoTab.index) intoSlots.first else intoSlots.last

        val transaction =
            player.invTransaction(bank) {
                val bankInv = select(bank)
                bulkShift {
                    this.from = bankInv
                    this.fromSlots = fromSlots
                    this.intoSlot = intoSlot
                }
            }

        if (transaction.failure) {
            return
        }

        val fromTabSize = fromTab.occupiedSpace(this)
        if (fromTab.index > intoTab.index) {
            val tabShiftRange = fromTab.index - 1 downTo intoTab.index
            for (index in tabShiftRange) {
                val curr = BankTab.forIndex(index)
                checkNotNull(curr) { "`curr` tab should not be null: $index" }

                val next = BankTab.forIndex(index + 1)
                checkNotNull(next) { "`next` tab should not be null: ${index + 1}" }

                vars[next.sizeVarBit] = vars[curr.sizeVarBit]
            }
        } else {
            val tabShiftRange = fromTab.index until intoTab.index
            for (index in tabShiftRange) {
                val curr = BankTab.forIndex(index)
                checkNotNull(curr) { "`curr` tab should not be null: $index" }

                val next = BankTab.forIndex(index + 1)
                checkNotNull(next) { "`next` tab should not be null: ${index + 1}" }

                vars[curr.sizeVarBit] = vars[next.sizeVarBit]
            }
        }
        vars[intoTab.sizeVarBit] = fromTabSize
    }

    private fun ProtectedAccess.dragSideInv(drag: IfModalDrag) {
        val fromSlot = drag.selectedSlot ?: return resendSlot(inv, 0)
        val intoSlot = drag.targetSlot ?: return resendSlot(inv, 0)
        invMoveToSlot(inv, inv, fromSlot, intoSlot)
        setBanksideExtraOps()
    }

    private fun ProtectedAccess.dragMainInv(drag: IfModalDrag) {
        val fromSlot = drag.selectedSlot ?: return resendSlot(bank, 0)
        val intoSlot = drag.targetSlot ?: return resendSlot(bank, 0)
        val fromTab = BankTab.forSlot(this, fromSlot) ?: return resendSlot(bank, fromSlot)

        // Intercept drag buttons that target tab "extended" slots. These are special subcomponents
        // created by the client to represent tab slots beyond their current capacity. (e.g., the
        // slot right next to the last obj in a tab)
        val extendedTabSlots = bank_comsubs.tab_extended_slots_offset.offset(bank.size - 1)
        if (intoSlot in extendedTabSlots) {
            val tabIndex = intoSlot - extendedTabSlots.first
            val intoTab = BankTab.forIndex(tabIndex - 1) ?: BankTab.Main

            // Any attempt to insert an obj into the "extended" slot of the tab it already belongs
            // to will be rejected and have its slot resynced.
            if (fromTab == intoTab) {
                resendSlot(bank, fromSlot)
                return
            }

            dragMainInvExtendedSlot(fromTab, fromSlot, intoTab)
            return
        }

        val intoTab = BankTab.forSlot(this, intoSlot) ?: BankTab.Main
        dragMainInv(fromTab, fromSlot, intoTab, intoSlot)
    }

    private fun ProtectedAccess.dragMainInvExtendedSlot(
        fromTab: BankTab,
        fromSlot: Int,
        intoTab: BankTab,
    ) {
        val intoSlots = intoTab.slotRange(this)
        val emptySlot = intoSlots.firstOrNull { bank[it] == null }
        val intoSlot = emptySlot ?: intoSlots.last
        val intoSlotOffset = if (emptySlot == null && fromSlot > intoSlot) 1 else 0
        val targetSlot = intoSlot + intoSlotOffset
        val targetSlotEmpty = bank[targetSlot] == null

        val transaction =
            player.invTransaction(bank) {
                val bankInv = select(bank)
                if (!targetSlotEmpty) {
                    shiftInsert {
                        this.from = bankInv
                        this.fromSlot = fromSlot
                        this.intoSlot = targetSlot
                    }
                } else {
                    swap {
                        this.from = bankInv
                        this.into = bankInv
                        this.fromSlot = fromSlot
                        this.intoSlot = targetSlot
                    }
                    leftShift {
                        this.from = bankInv
                        this.startSlot = fromSlot + 1
                        this.toSlot = fromSlot
                    }
                }
            }

        if (transaction.failure) {
            resendSlot(bank, fromSlot)
            return
        }

        fromTab.decreaseSize(this)
        if (emptySlot == null) {
            intoTab.increaseSize(this)
        }

        notifySlotUpdate(fromTab)
        if (fromTab.isEmpty(this)) {
            compactEmptyTabs(fromTab.index)
        }
    }

    private fun ProtectedAccess.dragMainInv(
        fromTab: BankTab,
        fromSlot: Int,
        intoTab: BankTab,
        intoSlot: Int,
    ) {
        if (!insertMode) {
            dragMainInvSwap(fromTab, fromSlot, intoTab, intoSlot)
            return
        }

        if (fromTab == intoTab && abs(fromSlot - intoSlot) == 1) {
            dragMainInvSwap(fromTab, fromSlot, intoTab, intoSlot)
            return
        }

        dragMainInvShift(fromTab, fromSlot, intoTab, intoSlot)
    }

    private fun ProtectedAccess.dragMainInvSwap(
        fromTab: BankTab,
        fromSlot: Int,
        intoTab: BankTab,
        intoSlot: Int,
    ) {
        val intoSlots = intoTab.slotRange(this)

        val transaction =
            player.invTransaction(bank) {
                val bankInv = select(bank)
                swap {
                    this.from = bankInv
                    this.into = bankInv
                    this.fromSlot = fromSlot
                    this.intoSlot = intoSlot
                }
            }

        if (transaction.failure) {
            resendSlot(bank, fromSlot)
            return
        }

        // Allows for size expansion if an obj is dragged outside the tab's current capacity.
        // This is technically only possible for the main bank tab.
        val sizeExpansion = intoSlot - intoSlots.last
        if (sizeExpansion > 0) {
            intoTab.increaseSize(this, sizeExpansion)
        }

        notifySlotUpdate(fromSlot)
        if (fromTab.isEmpty(this)) {
            compactEmptyTabs(fromTab.index)
        }
    }

    private fun ProtectedAccess.dragMainInvShift(
        fromTab: BankTab,
        fromSlot: Int,
        intoTab: BankTab,
        intoSlot: Int,
    ) {
        val intoSameTab = fromTab == intoTab
        val intoEmptySlot = bank[intoSlot] == null
        val intoSlotOffset = if (!intoEmptySlot && fromSlot < intoSlot && !intoSameTab) 1 else 0
        val intoMainTabEmptySlot = fromTab.isMainTab && intoTab.isMainTab && intoEmptySlot
        val targetSlot = intoSlot - intoSlotOffset

        val transaction =
            player.invTransaction(bank) {
                val bankInv = select(bank)
                if (!intoEmptySlot) {
                    shiftInsert {
                        this.from = bankInv
                        this.fromSlot = fromSlot
                        this.intoSlot = targetSlot
                    }
                } else {
                    swap {
                        this.from = bankInv
                        this.into = bankInv
                        this.fromSlot = fromSlot
                        this.intoSlot = targetSlot
                    }
                    if (!intoMainTabEmptySlot) {
                        leftShift {
                            this.from = bankInv
                            this.startSlot = fromSlot + 1
                            this.toSlot = fromSlot
                        }
                    }
                }
            }

        if (transaction.failure) {
            resendSlot(bank, fromSlot)
            return
        }

        if (!intoMainTabEmptySlot) {
            fromTab.decreaseSize(this)
        }

        if (!intoEmptySlot) {
            intoTab.increaseSize(this)
        } else {
            // Allows for size expansion if an obj is dragged outside the tab's current capacity.
            // This is technically only possible for the main bank tab.
            val sizeExpansion = intoSlot - intoTab.slotRange(this).last
            if (sizeExpansion > 0) {
                intoTab.increaseSize(this, sizeExpansion)
            }
        }

        notifySlotUpdate(fromTab)
        if (fromTab.isEmpty(this)) {
            compactEmptyTabs(fromTab.index)
        }
    }

    private fun ProtectedAccess.dragIntoTab(drag: IfModalDrag) {
        val fromSlot = drag.selectedSlot ?: return resendSlot(bank, 0)
        val comsub = drag.targetSlot ?: return resendSlot(bank, 0)

        if (comsub != bank_comsubs.main_tab && comsub !in bank_comsubs.other_tabs) {
            resendSlot(bank, fromSlot)
            return
        }

        val obj = bank[fromSlot]
        if (obj == null || obj.id != drag.selectedObj) {
            resendSlot(bank, fromSlot)
            return
        }

        val fromTab = BankTab.forSlot(this, fromSlot) ?: return resendSlot(bank, 0)
        if (comsub == bank_comsubs.main_tab) {
            dragIntoTab(fromTab, fromSlot, BankTab.Main)
            return
        }

        val tabIndex = comsub - bank_comsubs.other_tabs.first
        val intoTab = BankTab.forIndex(tabIndex) ?: return resendSlot(bank, fromSlot)
        dragIntoTab(fromTab, fromSlot, intoTab)
    }

    private fun ProtectedAccess.dragIntoTab(fromTab: BankTab, fromSlot: Int, intoTab: BankTab) {
        if (intoTab == fromTab) {
            resendSlot(bank, fromSlot)
            return
        }
        dragMainInvExtendedSlot(fromTab, fromSlot, intoTab)
    }

    private fun ProtectedAccess.selectTab(comsub: Int, op: IfButtonOp) {
        val tabIndex = comsub - bank_comsubs.other_tabs.first
        val tab = BankTab.forIndex(tabIndex) ?: BankTab.Main
        if (tab == BankTab.Main) {
            if (op == IfButtonOp.Op1) {
                selectedTab = tab
            } else if (op == IfButtonOp.Op7) {
                removePlaceholders(tab)
            }
            return
        }

        if (tab.isEmpty(this)) {
            mes("To create a new tab, drag items from your bank onto this tab.")
            return
        }

        mesLayerClose(player, constants.meslayer_mode_objsearch)
        when (op) {
            IfButtonOp.Op1 -> selectedTab = tab
            IfButtonOp.Op6 -> collapseTab(tab)
            IfButtonOp.Op7 -> removePlaceholders(tab)
            else -> throw NotImplementedError("Bank tab op not implemented: op=$op")
        }
    }

    private fun ProtectedAccess.removePlaceholders(tab: BankTab) {
        val slots = tab.slotRange(this)
        if (slots.isEmpty()) {
            mes("You don't have any placeholders to release.")
            return
        }
        var removed = 0
        for (slot in slots) {
            val obj = bank[slot] ?: continue
            if (objTypes[obj].isPlaceholder) {
                bank[slot] = null
                removed++
            }
        }
        if (removed == 0) {
            mes("You don't have any placeholders to release.")
            return
        }
        notifySlotUpdate(slots.first)
    }

    private fun ProtectedAccess.removePlaceholders() {
        val tabUpdates = mutableSetOf<BankTab>()
        for (slot in bank.indices) {
            val obj = bank[slot] ?: continue
            if (objTypes[obj].isPlaceholder) {
                bank[slot] = null

                val tab = BankTab.forSlot(this, slot)
                tab?.let(tabUpdates::add)
            }
        }
        if (tabUpdates.isEmpty()) {
            return
        }
        soundSynth(synths.paper_turn)
        for (tab in tabUpdates) {
            notifySlotUpdate(tab)
        }
    }

    private suspend fun ProtectedAccess.removeFillers() {
        val confirmation =
            confirmOverlay(
                target = bank_components.confirmation_overlay_target,
                title = "Clear all fillers?",
                text =
                    "This option will clear <col=ffb83f>all</col> the bank fillers throughout " +
                        "your <col=ffb83f>whole bank</col>.<br><br>Are you sure?",
                cancel = "<col=ff0000>Cancel</col>",
                confirm = "<col=0dc10d>Clear them all</col>",
            )
        if (!confirmation) {
            return
        }
        val tabUpdates = mutableSetOf<BankTab>()
        for (slot in bank.indices) {
            val obj = bank[slot] ?: continue
            if (objTypes[obj].isType(bank_objs.filler)) {
                bank[slot] = null

                val tab = BankTab.forSlot(this, slot)
                tab?.let(tabUpdates::add)
            }
        }
        soundSynth(synths.paper_turn)
        for (tab in tabUpdates) {
            notifySlotUpdate(tab)
        }
    }

    private fun ProtectedAccess.collapseTab(tab: BankTab) {
        require(!tab.isMainTab) { "Main bank tab cannot be collapsed." }

        for (tab in BankTab.tabs) {
            compressTabObjs(tab)
        }

        val collapseObjCount = tab.occupiedSpace(this)
        val collapseTabSlots = tab.slotRange(this)

        val targetTab = BankTab.Main
        val targetTabSlots = targetTab.slotRange(this)
        val targetEmptySlots =
            targetTabSlots.asSequence().filter { bank[it] == null }.take(collapseObjCount).toList()

        val newSlotsCount = collapseObjCount - targetEmptySlots.size
        val swapQuery =
            player.invTransaction(bank) {
                val bankInv = select(bank)
                for (i in targetEmptySlots.indices) {
                    val fromSlot = collapseTabSlots.first + i
                    if (fromSlot > collapseTabSlots.last) {
                        break
                    }
                    swap {
                        this.from = bankInv
                        this.into = bankInv
                        this.fromSlot = fromSlot
                        this.intoSlot = targetEmptySlots[i]
                        this.strict = true
                    }
                }
                if (newSlotsCount > 0) {
                    val shiftStartSlot = collapseTabSlots.first + targetEmptySlots.size
                    val shiftSlots = shiftStartSlot..collapseTabSlots.last
                    bulkShift {
                        this.from = bankInv
                        this.fromSlots = shiftSlots
                        this.intoSlot = targetTabSlots.last
                    }
                }
            }
        check(swapQuery.success) { "Could not collapse tab: tab=$tab, err=${swapQuery.err}" }

        targetTab.increaseSize(this, newSlotsCount)

        val trailingNullCount = trimGapsAndReturnTrailingGaps(tab)
        tab.decreaseSize(this, trailingNullCount)
        if (collapseObjCount > trailingNullCount) {
            tab.decreaseSize(this, collapseObjCount - trailingNullCount)
        }

        compactEmptyTabs(tab.index)
        if (selectedTab == tab) {
            selectedTab = BankTab.Main
        }
    }

    private fun ProtectedAccess.notifySlotUpdate(slot: Int) {
        val tab = BankTab.forSlot(this, slot)
        checkNotNull(tab) { "`slot` was not associated with a valid bank tab: $slot" }
        notifySlotUpdate(tab)
    }

    private fun ProtectedAccess.notifySlotUpdate(tab: BankTab) {
        val trailingNullCount = trimGapsAndReturnTrailingGaps(tab)
        if (trailingNullCount == 0) {
            return
        }

        tab.decreaseSize(this, trailingNullCount)

        if (tab.isEmpty(this) && !tab.isMainTab) {
            compactEmptyTabs(tab.index)

            if (selectedTab == tab) {
                selectedTab = BankTab.Main
            }
        }
    }

    private fun ProtectedAccess.trimGapsAndReturnTrailingGaps(slotRange: IntRange): Int {
        // Note: `shiftLeadingGapsToTail` directly modifies `bank` obj array.
        val result = BankSlots.shiftLeadingGapsToTail(bank, slotRange)

        val trailingNullCount = result.trailingGaps
        if (trailingNullCount == 0) {
            return 0
        }

        val shiftStartSlot = min(bank.size - 1, slotRange.last + 1)
        val shiftToSlot = min(bank.size - 1, slotRange.last + 1 - trailingNullCount)

        // Set a "max shift slot" to avoid unnecessary shifting of slots beyond the last occupied
        // slot in bank inventory.
        val maxShiftSlot = min(bank.size, bank.lastOccupiedSlot() + trailingNullCount)

        val shiftQuery =
            player.invTransaction(bank) {
                val bankInv = select(bank)
                leftShift {
                    this.from = bankInv
                    this.startSlot = shiftStartSlot
                    this.toSlot = shiftToSlot
                    this.maxSlot = maxShiftSlot
                }
            }

        check(shiftQuery.success) {
            "Could not shift bank inventory: " +
                "startSlot=$shiftStartSlot, toSlot=$shiftToSlot, err=${shiftQuery.err}"
        }

        return trailingNullCount
    }

    private fun ProtectedAccess.trimGapsAndReturnTrailingGaps(tab: BankTab): Int {
        val slots = tab.slotRange(this)
        return trimGapsAndReturnTrailingGaps(slots)
    }

    private fun ProtectedAccess.compressTabObjs(tab: BankTab) {
        if (tab.isEmpty(this)) {
            return
        }
        val tabSlots = tab.slotRange(this)
        if (tabSlots.first != tabSlots.last) {
            val compactQuery = player.invCompress(bank, tabSlots)
            check(compactQuery.success) {
                "Could not compress tab: tab=$tab, err=${compactQuery.err}"
            }
        }
        val trailingNullCount = tabSlots.count { bank[it] == null }
        if (trailingNullCount > 0) {
            tab.decreaseSize(this, trailingNullCount)
        }
    }

    private fun ProtectedAccess.compactEmptyTabs(startTabIndex: Int) {
        for (index in startTabIndex until BankTab.tabs.size) {
            val curr = BankTab.tabs[index]
            val next = BankTab.tabs.getOrNull(index + 1)
            val nextSize = next?.occupiedSpace(this) ?: 0
            vars[curr.sizeVarBit] = nextSize
        }
    }

    private fun ProtectedAccess.compressBank() {
        for (tab in BankTab.entries) {
            val slots = tab.slotRange(this)
            compressTabObjs(tab)
            trimGapsAndReturnTrailingGaps(slots)
        }
    }

    private fun ProtectedAccess.resolveLeftClickQty(): Int =
        when (leftClickQtyMode) {
            QuantityMode.One -> 1
            QuantityMode.Five -> 5
            QuantityMode.Ten -> 10
            QuantityMode.X -> resolveLastDepositQty()
            QuantityMode.All -> Int.MAX_VALUE
        }

    private fun ProtectedAccess.resolveLastDepositQty(): Int = max(1, lastQtyInput)

    private fun ProtectedAccess.incinerate(comsub: Int, objType: UnpackedObjType?) {
        if (objType == null || objType.isPlaceholder) {
            return
        }
        val slot = comsub - 1
        val obj = bank[slot] ?: return resendSlot(bank, 0)

        if (!obj.isType(objType)) {
            resendSlot(bank, slot)
            return
        }

        bank[slot] = null
        soundSynth(synths.firebreath)
        notifySlotUpdate(slot)

        val dispose = HeldDropEvents.Dispose(player, invs.bank, slot, obj)
        publish(dispose)
    }

    private fun ProtectedAccess.setBanksideExtraOps() {
        player.setBanksideExtraOps(objTypes)
    }

    private fun ProtectedAccess.setBankWornBonuses() {
        player.setBankWornBonuses(wornBonuses, weaponSpeeds)
    }

    private suspend fun ProtectedAccess.clickBanksideExtraOp(slot: Int) {
        val obj = inv[slot] ?: return resendSlot(inv, 0)
        val type = objTypes[obj]

        val isWearable = type.wearpos1 != -1
        if (isWearable) {
            val wearOpIndex = type.param(params.wear_op_index)
            val hasWearOp = type.hasInvOp(wearOpIndex)
            if (hasWearOp) {
                val result = invEquip(slot)
                if (result is HeldEquipResult.Fail) {
                    result.messages.forEach(::mes)
                    return
                }
                setBanksideExtraOps()
                setBankWornBonuses()
            }
            return
        }

        val isFood = type.isContentType(content.food)
        val isPotion = type.isContentType(content.potion)

        val isConsumable = isFood || isPotion
        if (isConsumable) {
            opHeld1(slot)
            return
        }

        val event = HeldBanksideEvents.Type(player, slot, type)
        publish(event)
    }

    private fun ProtectedAccess.wornOp(wornSlot: Int, op: IfButtonOp) {
        if (op == IfButtonOp.Op10) {
            objExamine(worn, wornSlot)
            return
        }
        val obj = worn[wornSlot] ?: return resendSlot(worn, wornSlot)

        if (op == IfButtonOp.Op2) {
            val type = objTypes[obj]
            val deposited = invDeposit(wornSlot, obj.count, worn)
            if (deposited) {
                val wearpos = Wearpos[type.wearpos1]
                checkNotNull(wearpos) {
                    "Wearpos should not be null for worn obj: " +
                        "wornSlot=$wornSlot, obj=$obj, type=$type"
                }
                WornUnequipOp.notifyWornUnequip(player, wearpos, type, eventBus)
            }
            return
        }

        if (op == IfButtonOp.Op1) {
            val result = wornUnequip(wornSlot)
            if (result is WornUnequipResult.Fail) {
                result.message?.let(::mes)
                return
            }
            setBanksideExtraOps()
            setBankWornBonuses()
            return
        }
    }

    suspend fun releasePlaceholders(access: ProtectedAccess) {
        val confirmation =
            access.confirmOverlay(
                target = bank_components.confirmation_overlay_target,
                title = "Release all placeholders?",
                text =
                    "This option will release <col=ffb83f>all</col> the placeholders " +
                        "throughout your <col=ffb83f>whole bank</col>." +
                        "<br><br>Are you sure?",
                cancel = "<col=ff0000>Cancel</col>",
                confirm = "<col=0dc10d>Release them all</col>",
            )
        if (confirmation) {
            access.removePlaceholders()
        }
    }

    suspend fun addBankFillers(access: ProtectedAccess, requestedCount: Int?) {
        val bank = access.bank
        val bankCapacity = access.bankCapacity

        val freeSpace = bankCapacity - bank.occupiedSpace()
        if (freeSpace <= 0) {
            access.mes("Your bank is already full, so there is no reason to add any bank fillers.")
            return
        }

        val count = requestedCount ?: access.countDialog()
        val cappedCount = min(freeSpace, count)
        if (cappedCount == 0) {
            return
        }

        val targetTab = BankTab.Main
        val startSlot = targetTab.slotRange(access).first
        val emptySlots = (startSlot until bankCapacity).filter { bank[it] == null }

        var completed = 0
        for (slot in emptySlots) {
            if (completed >= count) {
                break
            }
            if (bank[slot] == null) {
                // Give the filler vars to avoid any sort of merging through later transactions.
                val filler = InvObj(bank_objs.filler, vars = 1)
                bank[slot] = filler
                completed++
            }
        }

        if (completed == 0) {
            access.mes("Your bank is already full, so there is no reason to add any bank fillers.")
        } else {
            val formatCount = if (completed == 1) "bank filler" else "bank fillers"
            access.mes("You add $completed $formatCount to your bank.")
            targetTab.increaseSize(access, completed)
        }
    }

    private fun Player.setDefaultCapacity() {
        if (bankCapacity == 0) {
            bankCapacity = bank_constants.default_capacity
        }
    }
}
