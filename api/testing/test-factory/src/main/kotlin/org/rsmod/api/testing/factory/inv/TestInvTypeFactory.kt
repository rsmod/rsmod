package org.rsmod.api.testing.factory.inv

import org.rsmod.game.type.inv.InvScope
import org.rsmod.game.type.inv.InvTypeBuilder
import org.rsmod.game.type.inv.UnpackedInvType

public class TestInvTypeFactory {
    public fun create(id: Int = 0, init: InvTypeBuilder.() -> Unit = {}): UnpackedInvType =
        InvTypeBuilder("test_inv_type").apply(init).build(id)

    public fun createInv(id: Int = 93): UnpackedInvType =
        create(id) {
            scope = InvScope.Perm
            protect = false
            size = 28
        }

    public fun createWorn(id: Int = 94): UnpackedInvType =
        create(id) {
            scope = InvScope.Perm
            runWeight = true
            protect = false
            size = 14
        }
}
