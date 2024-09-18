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
    public var autoSize: Boolean = false

    public var stock: MutableList<InvStock?> = mutableListOf()

    public var stock1: InvStock?
        get() = stock[0]
        set(value) {
            stock = stock.insertAt(0, value)
        }

    public var stock2: InvStock?
        get() = stock[1]
        set(value) {
            stock = stock.insertAt(1, value)
        }

    public var stock3: InvStock?
        get() = stock[2]
        set(value) {
            stock = stock.insertAt(2, value)
        }

    public var stock4: InvStock?
        get() = stock[3]
        set(value) {
            stock = stock.insertAt(3, value)
        }

    public var stock5: InvStock?
        get() = stock[4]
        set(value) {
            stock = stock.insertAt(4, value)
        }

    public var stock6: InvStock?
        get() = stock[5]
        set(value) {
            stock = stock.insertAt(5, value)
        }

    public var stock7: InvStock?
        get() = stock[6]
        set(value) {
            stock = stock.insertAt(6, value)
        }

    public var stock8: InvStock?
        get() = stock[7]
        set(value) {
            stock = stock.insertAt(7, value)
        }

    public var stock9: InvStock?
        get() = stock[8]
        set(value) {
            stock = stock.insertAt(8, value)
        }

    public var stock10: InvStock?
        get() = stock[9]
        set(value) {
            stock = stock.insertAt(9, value)
        }

    public var stock11: InvStock?
        get() = stock[10]
        set(value) {
            stock = stock.insertAt(10, value)
        }

    public var stock12: InvStock?
        get() = stock[11]
        set(value) {
            stock = stock.insertAt(11, value)
        }

    public var stock13: InvStock?
        get() = stock[12]
        set(value) {
            stock = stock.insertAt(12, value)
        }

    public var stock14: InvStock?
        get() = stock[13]
        set(value) {
            stock = stock.insertAt(13, value)
        }

    public var stock15: InvStock?
        get() = stock[14]
        set(value) {
            stock = stock.insertAt(14, value)
        }

    public var stock16: InvStock?
        get() = stock[15]
        set(value) {
            stock = stock.insertAt(15, value)
        }

    public var stock17: InvStock?
        get() = stock[16]
        set(value) {
            stock = stock.insertAt(16, value)
        }

    public var stock18: InvStock?
        get() = stock[17]
        set(value) {
            stock = stock.insertAt(17, value)
        }

    public var stock19: InvStock?
        get() = stock[18]
        set(value) {
            stock = stock.insertAt(18, value)
        }

    public var stock20: InvStock?
        get() = stock[19]
        set(value) {
            stock = stock.insertAt(19, value)
        }

    public var stock21: InvStock?
        get() = stock[20]
        set(value) {
            stock = stock.insertAt(20, value)
        }

    public var stock22: InvStock?
        get() = stock[21]
        set(value) {
            stock = stock.insertAt(21, value)
        }

    public var stock23: InvStock?
        get() = stock[22]
        set(value) {
            stock = stock.insertAt(22, value)
        }

    public var stock24: InvStock?
        get() = stock[23]
        set(value) {
            stock = stock.insertAt(23, value)
        }

    public var stock25: InvStock?
        get() = stock[24]
        set(value) {
            stock = stock.insertAt(24, value)
        }

    public var stock26: InvStock?
        get() = stock[25]
        set(value) {
            stock = stock.insertAt(25, value)
        }

    public var stock27: InvStock?
        get() = stock[26]
        set(value) {
            stock = stock.insertAt(26, value)
        }

    public var stock28: InvStock?
        get() = stock[27]
        set(value) {
            stock = stock.insertAt(27, value)
        }

    public var stock29: InvStock?
        get() = stock[28]
        set(value) {
            stock = stock.insertAt(28, value)
        }

    public var stock30: InvStock?
        get() = stock[29]
        set(value) {
            stock = stock.insertAt(29, value)
        }

    public var stock31: InvStock?
        get() = stock[30]
        set(value) {
            stock = stock.insertAt(30, value)
        }

    public var stock32: InvStock?
        get() = stock[31]
        set(value) {
            stock = stock.insertAt(31, value)
        }

    public var stock33: InvStock?
        get() = stock[32]
        set(value) {
            stock = stock.insertAt(32, value)
        }

    public var stock34: InvStock?
        get() = stock[33]
        set(value) {
            stock = stock.insertAt(33, value)
        }

    public var stock35: InvStock?
        get() = stock[34]
        set(value) {
            stock = stock.insertAt(34, value)
        }

    public var stock36: InvStock?
        get() = stock[35]
        set(value) {
            stock = stock.insertAt(35, value)
        }

    public var stock37: InvStock?
        get() = stock[36]
        set(value) {
            stock = stock.insertAt(36, value)
        }

    public var stock38: InvStock?
        get() = stock[37]
        set(value) {
            stock = stock.insertAt(37, value)
        }

    public var stock39: InvStock?
        get() = stock[38]
        set(value) {
            stock = stock.insertAt(38, value)
        }

    public var stock40: InvStock?
        get() = stock[39]
        set(value) {
            stock = stock.insertAt(39, value)
        }

    public fun build(id: Int): UnpackedInvType {
        backing.internal = internal
        if (stock.isNotEmpty()) {
            backing.stock = stock.toTypedArray()
        }
        if (autoSize) {
            backing.size = stock.size
        }
        return backing.build(id)
    }

    public fun stock(type: ObjType, count: Int, restockCycles: Int): InvStock =
        InvStock(type, count, restockCycles)

    private fun MutableList<InvStock?>.insertAt(
        index: Int,
        stock: InvStock?,
    ): MutableList<InvStock?> =
        if (index == size) {
            apply { this += stock }
        } else {
            val name = "stock${size + 1}"
            throw IndexOutOfBoundsException("You must fill out `$name` first.")
        }
}
