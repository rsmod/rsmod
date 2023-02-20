package org.rsmod.plugins.profile.dispatch.player

import com.github.michaelbull.logging.InlineLogger
import org.rsmod.game.events.EventBus
import org.rsmod.game.model.mob.list.PlayerList
import org.rsmod.game.model.mob.list.anyNotNull
import org.rsmod.plugins.api.model.event.PlayerSession
import org.rsmod.plugins.profile.dispatch.transaction.TransactionDispatch
import javax.inject.Inject
import javax.inject.Singleton

private val logger = InlineLogger()

@Singleton
public class PlayerRegisterDispatch @Inject constructor(
    private val playerList: PlayerList,
    private val eventBus: EventBus
) : TransactionDispatch<PlayerDispatchRequest, PlayerRegisterResponse>() {

    internal fun serve(): Unit = super.serve(TRANSACTIONS_PER_SERVE)

    override fun serve(request: PlayerDispatchRequest): PlayerRegisterResponse {
        logger.debug { "Serve player registration response for request $request." }
        val index = playerList.nextAvailableIndex() ?: return PlayerRegisterResponse.NoAvailableIndex
        val player = request.player
        if (playerList.anyNotNull { it.username == player.username }) {
            return PlayerRegisterResponse.AlreadyOnline
        }
        player.index = index
        playerList[index] = player
        eventBus += PlayerSession.Initialize(player)
        eventBus += PlayerSession.LogIn(player)
        return PlayerRegisterResponse.Success
    }

    private companion object {

        private const val TRANSACTIONS_PER_SERVE = 25
    }
}
