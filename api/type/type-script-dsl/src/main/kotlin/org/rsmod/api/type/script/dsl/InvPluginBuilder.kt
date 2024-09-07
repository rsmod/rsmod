package org.rsmod.api.type.script.dsl

import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvStackType
import org.rsmod.game.type.inv.InvStock
import org.rsmod.game.type.inv.InvTypeBuilder
import org.rsmod.game.type.inv.UnpackedInvType
import org.rsmod.game.type.obj.ObjType

@DslMarker private annotation class InvBuilderDsl

@InvBuilderDsl
public class InvPluginBuilder(public var internal: String? = null) {
    private val backing: InvTypeBuilder = InvTypeBuilder()

    public var scope: InvScope? by backing::scope
    public var stack: InvStackType? by backing::stack
    public var size: Int? by backing::size
    public var restock: Boolean? by backing::restock
    public var allStock: Boolean? by backing::allStock
    public var protect: Boolean? by backing::protect
    public var runWeight: Boolean? by backing::runWeight
    public var dummyInv: Boolean? by backing::dummyInv
    public var placeholders: Boolean? by backing::placeholders

    private var invStockList: MutableList<InvStock?>? = null

    public var stock1: InvStock?
        get() = invStockList?.get(0)
        set(value) {
            invStockList = invStockList.insertAt(0, value)
        }

    public var stock2: InvStock?
        get() = invStockList?.get(1)
        set(value) {
            invStockList = invStockList.insertAt(1, value)
        }

    public var stock3: InvStock?
        get() = invStockList?.get(2)
        set(value) {
            invStockList = invStockList.insertAt(2, value)
        }

    public var stock4: InvStock?
        get() = invStockList?.get(3)
        set(value) {
            invStockList = invStockList.insertAt(3, value)
        }

    public var stock5: InvStock?
        get() = invStockList?.get(4)
        set(value) {
            invStockList = invStockList.insertAt(4, value)
        }

    public var stock6: InvStock?
        get() = invStockList?.get(5)
        set(value) {
            invStockList = invStockList.insertAt(5, value)
        }

    public var stock7: InvStock?
        get() = invStockList?.get(6)
        set(value) {
            invStockList = invStockList.insertAt(6, value)
        }

    public var stock8: InvStock?
        get() = invStockList?.get(7)
        set(value) {
            invStockList = invStockList.insertAt(7, value)
        }

    public var stock9: InvStock?
        get() = invStockList?.get(8)
        set(value) {
            invStockList = invStockList.insertAt(8, value)
        }

    public var stock10: InvStock?
        get() = invStockList?.get(9)
        set(value) {
            invStockList = invStockList.insertAt(9, value)
        }

    public var stock11: InvStock?
        get() = invStockList?.get(10)
        set(value) {
            invStockList = invStockList.insertAt(10, value)
        }

    public var stock12: InvStock?
        get() = invStockList?.get(11)
        set(value) {
            invStockList = invStockList.insertAt(11, value)
        }

    public var stock13: InvStock?
        get() = invStockList?.get(12)
        set(value) {
            invStockList = invStockList.insertAt(12, value)
        }

    public var stock14: InvStock?
        get() = invStockList?.get(13)
        set(value) {
            invStockList = invStockList.insertAt(13, value)
        }

    public var stock15: InvStock?
        get() = invStockList?.get(14)
        set(value) {
            invStockList = invStockList.insertAt(14, value)
        }

    public var stock16: InvStock?
        get() = invStockList?.get(15)
        set(value) {
            invStockList = invStockList.insertAt(15, value)
        }

    public var stock17: InvStock?
        get() = invStockList?.get(16)
        set(value) {
            invStockList = invStockList.insertAt(16, value)
        }

    public var stock18: InvStock?
        get() = invStockList?.get(17)
        set(value) {
            invStockList = invStockList.insertAt(17, value)
        }

    public var stock19: InvStock?
        get() = invStockList?.get(18)
        set(value) {
            invStockList = invStockList.insertAt(18, value)
        }

    public var stock20: InvStock?
        get() = invStockList?.get(19)
        set(value) {
            invStockList = invStockList.insertAt(19, value)
        }

    public var stock21: InvStock?
        get() = invStockList?.get(20)
        set(value) {
            invStockList = invStockList.insertAt(20, value)
        }

    public var stock22: InvStock?
        get() = invStockList?.get(21)
        set(value) {
            invStockList = invStockList.insertAt(21, value)
        }

    public var stock23: InvStock?
        get() = invStockList?.get(22)
        set(value) {
            invStockList = invStockList.insertAt(22, value)
        }

    public var stock24: InvStock?
        get() = invStockList?.get(23)
        set(value) {
            invStockList = invStockList.insertAt(23, value)
        }

    public var stock25: InvStock?
        get() = invStockList?.get(24)
        set(value) {
            invStockList = invStockList.insertAt(24, value)
        }

    public var stock26: InvStock?
        get() = invStockList?.get(25)
        set(value) {
            invStockList = invStockList.insertAt(25, value)
        }

    public var stock27: InvStock?
        get() = invStockList?.get(26)
        set(value) {
            invStockList = invStockList.insertAt(26, value)
        }

    public var stock28: InvStock?
        get() = invStockList?.get(27)
        set(value) {
            invStockList = invStockList.insertAt(27, value)
        }

    public var stock29: InvStock?
        get() = invStockList?.get(28)
        set(value) {
            invStockList = invStockList.insertAt(28, value)
        }

    public var stock30: InvStock?
        get() = invStockList?.get(29)
        set(value) {
            invStockList = invStockList.insertAt(29, value)
        }

    public var stock31: InvStock?
        get() = invStockList?.get(30)
        set(value) {
            invStockList = invStockList.insertAt(30, value)
        }

    public var stock32: InvStock?
        get() = invStockList?.get(31)
        set(value) {
            invStockList = invStockList.insertAt(31, value)
        }

    public var stock33: InvStock?
        get() = invStockList?.get(32)
        set(value) {
            invStockList = invStockList.insertAt(32, value)
        }

    public var stock34: InvStock?
        get() = invStockList?.get(33)
        set(value) {
            invStockList = invStockList.insertAt(33, value)
        }

    public var stock35: InvStock?
        get() = invStockList?.get(34)
        set(value) {
            invStockList = invStockList.insertAt(34, value)
        }

    public var stock36: InvStock?
        get() = invStockList?.get(35)
        set(value) {
            invStockList = invStockList.insertAt(35, value)
        }

    public var stock37: InvStock?
        get() = invStockList?.get(36)
        set(value) {
            invStockList = invStockList.insertAt(36, value)
        }

    public var stock38: InvStock?
        get() = invStockList?.get(37)
        set(value) {
            invStockList = invStockList.insertAt(37, value)
        }

    public var stock39: InvStock?
        get() = invStockList?.get(38)
        set(value) {
            invStockList = invStockList.insertAt(38, value)
        }

    public var stock40: InvStock?
        get() = invStockList?.get(39)
        set(value) {
            invStockList = invStockList.insertAt(39, value)
        }

    public fun build(id: Int): UnpackedInvType {
        backing.internal = internal
        invStockList?.let { stock -> backing.stock = stock.toTypedArray() }
        return backing.build(id)
    }

    public fun stock(type: ObjType, count: Int, restockTicks: Int): InvStock =
        InvStock(type, count, restockTicks)

    private fun MutableList<InvStock?>?.insertAt(
        index: Int,
        stock: InvStock?,
    ): MutableList<InvStock?> =
        if (this == null) {
            mutableListOf(stock)
        } else if (index == size) {
            apply { this += stock }
        } else {
            val name = "stock${size + 1}"
            throw IndexOutOfBoundsException("You must fill out `$name` first.")
        }
}
