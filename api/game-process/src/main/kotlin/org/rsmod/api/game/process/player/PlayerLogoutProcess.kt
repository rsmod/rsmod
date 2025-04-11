package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import org.rsmod.api.registry.account.AccountRegistry

public class PlayerLogoutProcess @Inject constructor(private val accountRegistry: AccountRegistry) {
    public fun process() {
        accountRegistry.handleLogouts()
    }
}
