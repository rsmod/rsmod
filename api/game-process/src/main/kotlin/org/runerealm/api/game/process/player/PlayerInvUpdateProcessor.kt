package org.runerealm.api.game.process.player

import jakarta.inject.Inject
import kotlin.collections.iterator
import org.runerealm.api.player.output.UpdateInventory
import org.runerealm.api.utils.logging.GameExceptionHandler
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.util.ShuffledPlayerList
import org.runerealm.game.inv.Inventory

public class PlayerInvUpdateProcessor
@Inject
constructor(
    private val players: ShuffledPlayerList,
    private val exceptionHandler: GameExceptionHandler,
) {
    private val processedInvs = hashSetOf<Inventory>()

    public fun process(player: Player) {
        player.updateTransmittedInvs()
        player.processQueuedTransmissions()
    }

    public fun cleanUp() {
        processedInvs.forEach(Inventory::clearModifiedSlots)
        processedInvs.clear()
    }

    private fun Player.updateTransmittedInvs() {
        for (transmitted in transmittedInvs.intIterator()) {
            val inv = invMap.backing[transmitted]
            checkNotNull(inv) { "Inv expected in `invMap`: $transmitted (invMap=${invMap})" }
            if (!inv.hasModifiedSlots()) {
                continue
            }
            UpdateInventory.updateInvPartial(this, inv)
            updatePendingRunWeight(inv)
            processedInvs += inv
        }
    }

    private fun Player.processQueuedTransmissions() {
        for (add in transmittedInvAddQueue.intIterator()) {
            val inv = invMap.backing[add]
            checkNotNull(inv) { "Inv expected in `invMap`: $add (invMap=${invMap})" }
            UpdateInventory.updateInvFull(this, inv)
            updatePendingRunWeight(inv)
            transmittedInvs.add(add)
            processedInvs += inv
        }
        transmittedInvAddQueue.clear()
    }

    private fun Player.updatePendingRunWeight(inventory: Inventory) {
        val updateRunWeight = inventory.type.runWeight
        if (updateRunWeight) {
            pendingRunWeight = true
        }
    }
}
