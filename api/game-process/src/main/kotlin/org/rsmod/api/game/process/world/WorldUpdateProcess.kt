package org.rsmod.api.game.process.world

import jakarta.inject.Inject
import org.rsmod.api.player.output.MiscOutput
import org.rsmod.game.GameUpdate
import org.rsmod.game.GameUpdate.Companion.isClear
import org.rsmod.game.GameUpdate.Companion.isCountdown
import org.rsmod.game.entity.PlayerList

public class WorldUpdateProcess
@Inject
constructor(private val update: GameUpdate, private val playerList: PlayerList) {
    private var syncTimer: Int = 0

    public fun process() {
        val state = update.state ?: return
        if (state.isCountdown()) {
            processCountdown(state)
        } else if (state.isClear()) {
            processClear()
        }
    }

    private fun processCountdown(countdown: GameUpdate.State.Countdown) {
        signalCountdown(countdown.current)
        countdown.current--
    }

    private fun signalCountdown(cycles: Int) {
        require(cycles >= 0) { "Countdown expected to be positive: $cycles" }

        if (cycles == 0) {
            triggerUpdate()
            return
        }

        if (++syncTimer % SYNC_INTERVALS == 0) {
            syncCountdown(cycles)
        }
    }

    private fun syncCountdown(cycles: Int) {
        for (player in playerList) {
            MiscOutput.updateRebootTimer(player, cycles)
        }
    }

    private fun triggerUpdate() {
        resetSyncTimer()
        update.setUpdating()
        for (player in playerList) {
            player.pendingShutdown = true
        }
    }

    private fun processClear() {
        resetSyncTimer()
        update.reset()
        for (player in playerList) {
            MiscOutput.clearUpdateRebootTimer(player)
        }
    }

    private fun resetSyncTimer() {
        syncTimer = 0
    }

    private companion object {
        /** The interval (in game cycles) to re-send the reboot timer packet to all players. */
        private const val SYNC_INTERVALS = 3
    }
}
