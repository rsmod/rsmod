package org.rsmod.content.interfaces.bank

import kotlin.math.max
import org.rsmod.api.player.protect.ProtectedAccess
import org.rsmod.api.utils.vars.VarEnumDelegate
import org.rsmod.content.interfaces.bank.configs.bank_varbits
import org.rsmod.game.type.varbit.VarBitType

enum class BankTab(val index: Int, val sizeVarBit: VarBitType, override val varValue: Int) :
    VarEnumDelegate {
    Tab1(0, bank_varbits.tab_size1, varValue = 1),
    Tab2(1, bank_varbits.tab_size2, varValue = 2),
    Tab3(2, bank_varbits.tab_size3, varValue = 3),
    Tab4(3, bank_varbits.tab_size4, varValue = 4),
    Tab5(4, bank_varbits.tab_size5, varValue = 5),
    Tab6(5, bank_varbits.tab_size6, varValue = 6),
    Tab7(6, bank_varbits.tab_size7, varValue = 7),
    Tab8(7, bank_varbits.tab_size8, varValue = 8),
    Tab9(8, bank_varbits.tab_size9, varValue = 9),
    Main(9, bank_varbits.tab_size_main, varValue = 0);

    val isMainTab: Boolean
        get() = this == Main

    fun firstSlot(access: ProtectedAccess): Int {
        val indexRange = 0 until index
        return indexRange.sumOf {
            val tab = entries[it]
            access.vars[tab.sizeVarBit]
        }
    }

    fun slotRange(access: ProtectedAccess): IntRange {
        val firstSlot = firstSlot(access)
        val occupiedSpace = occupiedSpace(access)
        return firstSlot until firstSlot + occupiedSpace
    }

    fun occupiedSpace(access: ProtectedAccess): Int = access.vars[sizeVarBit]

    fun isEmpty(access: ProtectedAccess): Boolean = occupiedSpace(access) == 0

    fun decreaseSize(access: ProtectedAccess, amount: Int = 1) {
        val size = access.vars[sizeVarBit]
        access.vars[sizeVarBit] = max(0, size - amount)
        assert(size >= amount) {
            "Decreased tab size with an amount higher than capacity: decrease=$amount, size=$size"
        }
    }

    fun increaseSize(access: ProtectedAccess, amount: Int = 1) {
        access.vars[sizeVarBit] += amount
    }

    companion object {
        init {
            val sorted = entries.sortedBy(BankTab::index)
            check(sorted == entries) { "Entries must be sorted by `index`." }
        }

        val tabs = entries - Main

        fun forIndex(index: Int): BankTab? = entries.getOrNull(index)

        fun forSlot(access: ProtectedAccess, slot: Int): BankTab? {
            var currSlot = 0
            for (tab in entries) {
                val size = access.vars[tab.sizeVarBit]
                if (slot in currSlot until currSlot + size) {
                    return tab
                }
                currSlot += size
            }
            return null
        }
    }
}
