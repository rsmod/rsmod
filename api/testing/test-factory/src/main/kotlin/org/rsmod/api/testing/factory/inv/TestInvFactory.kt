package org.rsmod.api.testing.factory.inv

import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.inv.UnpackedInvType

public class TestInvFactory {
    public fun create(type: UnpackedInvType): Inventory = Inventory.create(type)
}
