package org.runerealm.api.game.process.player

import jakarta.inject.Inject
import org.runerealm.api.registry.account.AccountRegistry

public class PlayerLoginProcess @Inject constructor(private val accountRegistry: AccountRegistry) {
    public fun process() {
        accountRegistry.handleLogins()
    }
}
