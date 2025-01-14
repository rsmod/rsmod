package org.rsmod.api.game.process.player

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.rsmod.api.player.forceDisconnect
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.seq.EntitySeq

public class PlayerPostTickProcess
@Inject
constructor(private val players: PlayerList, private val zoneUpdates: PlayerZoneUpdateProcessor) {
    private val logger = InlineLogger()

    public fun process() {
        players.processZones()
        players.forEach {
            it.tryOrDisconnect {
                flushClient()
                cleanUpPendingUpdates()
            }
        }
    }

    @Suppress("DeferredResultUnused")
    private fun PlayerList.processZones() = runBlocking {
        zoneUpdates.computeEnclosedBuffers()
        supervisorScope {
            for (player in this@processZones) {
                async { player.tryOrDisconnect { zoneUpdates.process(this) } }
            }
        }
        zoneUpdates.clearEnclosedBuffers()
        zoneUpdates.clearPendingZoneUpdates()
    }

    private fun Player.flushClient() {
        client.flush()
    }

    private fun Player.cleanUpPendingUpdates() {
        pendingSequence = EntitySeq.NULL
    }

    private inline fun Player.tryOrDisconnect(block: Player.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            forceDisconnect()
            logger.error(e) { "Error processing zone updates for player: $this." }
        } catch (e: NotImplementedError) {
            forceDisconnect()
            logger.error(e) { "Error processing zone updates for player: $this." }
        }
}
