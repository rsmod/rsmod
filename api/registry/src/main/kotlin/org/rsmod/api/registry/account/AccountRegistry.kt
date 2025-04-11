package org.rsmod.api.registry.account

import jakarta.inject.Inject
import java.util.concurrent.ConcurrentLinkedQueue
import org.rsmod.api.account.AccountManager
import org.rsmod.api.account.loader.request.AccountLoadResponse
import org.rsmod.api.account.saver.request.AccountSaveResponse
import org.rsmod.api.registry.player.PlayerRegistry
import org.rsmod.events.EventBus
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.player.SessionStateEvent

public class AccountRegistry
@Inject
constructor(
    private val eventBus: EventBus,
    private val playerRegistry: PlayerRegistry,
    private val accountManager: AccountManager,
) {
    private val pendingLogins = ConcurrentLinkedQueue<QueuedLogin>()
    private val pendingLogouts = ConcurrentLinkedQueue<QueuedLogout>()

    public fun queueLogin(
        player: Player,
        response: AccountLoadResponse.Ok,
        callback: (Player, AccountLoadResponse.Ok) -> Unit,
    ) {
        val queued = QueuedLogin(player, response, callback)
        pendingLogins.add(queued)
    }

    public fun queueLogout(player: Player) {
        accountManager.save(player, ::queueSaveResponse)
    }

    public fun handleLogins(count: Int = DEFAULT_LOGINS_PER_CYCLE) {
        var left = count
        while (left-- > 0) {
            val next = pendingLogins.poll() ?: break
            val (player, response, callback) = next
            callback(player, response)
        }
    }

    public fun handleLogouts(count: Int = DEFAULT_LOGOUTS_PER_CYCLE) {
        var left = count
        while (left-- > 0) {
            val next = pendingLogouts.poll() ?: break
            val (player, callback) = next
            callback(player)
        }
    }

    private fun queueSaveResponse(response: AccountSaveResponse) {
        val queued = QueuedLogout(response.player, ::handleLogout)
        pendingLogouts.add(queued)
        when (response) {
            is AccountSaveResponse.Success -> {}
            is AccountSaveResponse.ExcessiveRetries -> saveEmergencyBackup(response.player)
            is AccountSaveResponse.InternalShutdownError -> saveEmergencyBackup(response.player)
        }
    }

    private fun handleLogout(player: Player) {
        playerRegistry.del(player)
        eventBus.publish(SessionStateEvent.Terminate(player))
    }

    private fun saveEmergencyBackup(player: Player) {
        // TODO: Save an emergency backup file of player when the player save service is unable to
        //  do so.
    }

    private data class QueuedLogin(
        val player: Player,
        val response: AccountLoadResponse.Ok,
        val callback: (Player, AccountLoadResponse.Ok) -> Unit,
    )

    private data class QueuedLogout(val player: Player, val callback: (Player) -> Unit)

    private companion object {
        private const val DEFAULT_LOGINS_PER_CYCLE = 100
        private const val DEFAULT_LOGOUTS_PER_CYCLE = 200
    }
}
