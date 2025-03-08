package org.rsmod.api.specials.instant

import org.rsmod.api.player.protect.ProtectedAccess

public fun interface InstantSpecialAttack {
    public suspend fun ProtectedAccess.activate()
}
