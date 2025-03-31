package org.rsmod.api.testing.factory.inv

import org.rsmod.api.testing.factory.invTypeFactory
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.inv.UnpackedInvType

public class TestInvFactory {
    public fun create(type: UnpackedInvType): Inventory = Inventory.create(type)

    public fun createInv(type: UnpackedInvType = invTypeFactory.createInv()): Inventory =
        Inventory.create(type)

    public fun createWorn(type: UnpackedInvType = invTypeFactory.createWorn()): Inventory =
        Inventory.create(type)
}
