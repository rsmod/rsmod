package org.rsmod.api.game.process.player

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import kotlin.collections.iterator
import org.rsmod.api.player.forceDisconnect
import org.rsmod.api.player.output.UpdateInventory
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.inv.Inventory

public class PlayerInvUpdateProcess @Inject constructor(private val players: PlayerList) {
    private val logger = InlineLogger()

    private val processedInvs = hashSetOf<Inventory>()

    public fun process() {
        players.process()
        resetProcessedInvs()
    }

    private fun PlayerList.process() {
        for (player in this) {
            player.tryOrDisconnect {
                updateTransmittedInvs()
                processQueuedTransmissions()
            }
        }
    }

    private fun Player.updateTransmittedInvs() {
        for (transmitted in transmittedInvs.intIterator()) {
            val inv = invMap.backing[transmitted]
            checkNotNull(inv) { "Inv expected in `invMap`: $transmitted (invMap=${invMap})" }
            if (!inv.hasModifiedSlots()) {
                continue
            }
            UpdateInventory.updateInvPartial(this, inv)
            processedInvs += inv
        }
    }

    private fun Player.processQueuedTransmissions() {
        for (add in transmittedInvAddQueue.intIterator()) {
            val inv = invMap.backing[add]
            checkNotNull(inv) { "Inv expected in `invMap`: $add (invMap=${invMap})" }
            UpdateInventory.updateInvFull(this, inv)
            transmittedInvs.add(add)
            processedInvs += inv
        }
        transmittedInvAddQueue.clear()
    }

    private fun resetProcessedInvs() {
        processedInvs.forEach(Inventory::clearModifiedSlots)
        processedInvs.clear()
    }

    private inline fun Player.tryOrDisconnect(block: Player.() -> Unit) =
        try {
            block(this)
        } catch (e: Exception) {
            forceDisconnect()
            logger.error(e) { "Error processing inv updates for player: $this." }
        } catch (e: NotImplementedError) {
            forceDisconnect()
            logger.error(e) { "Error processing inv updates for player: $this." }
        }
}
