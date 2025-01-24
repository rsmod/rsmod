package org.rsmod.api.game.process.player

import com.github.michaelbull.logging.InlineLogger
import jakarta.inject.Inject
import kotlin.collections.iterator
import org.rsmod.api.player.forceDisconnect
import org.rsmod.api.player.output.UpdateInventory.updateInvPartial
import org.rsmod.game.entity.Player
import org.rsmod.game.entity.PlayerList
import org.rsmod.game.inv.Inventory
import org.rsmod.game.type.inv.InvScope

public class PlayerInvUpdateProcess @Inject constructor(private val players: PlayerList) {
    private val logger = InlineLogger()

    private val sharedUpdatedInvs = hashSetOf<Inventory>()

    public fun process() {
        players.process()
        resetUpdatedInvs()
    }

    private fun PlayerList.process() {
        for (player in this) {
            player.tryOrDisconnect { updateTransmittedInvs() }
        }
    }

    private fun Player.updateTransmittedInvs() {
        for (transmitted in transmittedInvs.intIterator()) {
            val inv = invMap.backing[transmitted]
            checkNotNull(inv) { "Inv expected in `invMap` cache: $transmitted (invMap=${invMap})" }
            updateInv(inv)
        }
    }

    private fun Player.updateInv(inv: Inventory) {
        if (!inv.hasModifiedSlots()) {
            return
        }
        updateInvPartial(this, inv)
        if (inv.type.scope == InvScope.Shared) {
            sharedUpdatedInvs += inv
        } else {
            inv.clearModifiedSlots()
        }
    }

    private fun resetUpdatedInvs() {
        sharedUpdatedInvs.forEach(Inventory::clearModifiedSlots)
        sharedUpdatedInvs.clear()
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
