package org.runerealm.api.testing.factory.inv

import org.runerealm.api.testing.factory.invTypeFactory
import org.runerealm.game.inv.Inventory
import org.runerealm.game.type.inv.UnpackedInvType

public class TestInvFactory {
    public fun create(type: UnpackedInvType): Inventory = Inventory.create(type)

    public fun createInv(type: UnpackedInvType = invTypeFactory.createInv()): Inventory =
        Inventory.create(type)

    public fun createWorn(type: UnpackedInvType = invTypeFactory.createWorn()): Inventory =
        Inventory.create(type)
}
