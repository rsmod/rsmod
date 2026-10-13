package org.runerealm.api.game.process.player

import jakarta.inject.Inject
import org.runerealm.api.registry.account.AccountRegistry
import org.runerealm.events.EventBus
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.player.SessionStateEvent

/*
 * This class is not strictly required, as it is not expected to grow and could have been inlined
 * into `PlayerLogoutProcess`. However, the split promotes a clear logical boundary between the
 * pending logout step and the actual logout logic.
 */
public class PlayerLogoutProcessor
@Inject
constructor(private val eventBus: EventBus, private val accountRegistry: AccountRegistry) {
    public fun process(player: Player) {
        player.completeLogout()
    }

    // Finalizes the logout process. The player remains in the player list until their account
    // save callback completes, which is guaranteed to occur.
    private fun Player.completeLogout() {
        eventBus.publish(SessionStateEvent.Logout(this))
        accountRegistry.queueLogout(this)
    }
}
