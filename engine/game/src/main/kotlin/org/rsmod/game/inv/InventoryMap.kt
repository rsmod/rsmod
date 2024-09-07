package org.rsmod.game.inv

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.rsmod.game.type.inv.InvType
import org.rsmod.game.type.inv.UnpackedInvType

public class InventoryMap(
    public val backing: MutableMap<Int, Inventory> = Int2ObjectOpenHashMap()
) {
    public val size: Int
        get() = backing.size

    public val values: Collection<Inventory>
        get() = backing.values

    public fun isEmpty(): Boolean = backing.isEmpty()

    public fun isNotEmpty(): Boolean = backing.isNotEmpty()

    public fun getOrPut(type: UnpackedInvType): Inventory {
        val inv = this[type]
        if (inv != null) {
            return inv
        }
        val create = Inventory.create(type)
        this[type] = create
        return create
    }

    public fun getValue(type: InvType): Inventory =
        this[type] ?: throw NoSuchElementException("InvType is missing in the map: $type.")

    public operator fun set(type: InvType, inventory: Inventory) {
        backing[type.id] = inventory
    }

    public operator fun get(type: InvType): Inventory? = backing.getOrDefault(type.internalId, null)

    public operator fun contains(type: InvType): Boolean = backing.containsKey(type.internalId)
}
