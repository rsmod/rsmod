package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import kotlin.math.max
import kotlin.math.min
import org.rsmod.api.config.constants
import org.rsmod.api.inv.weight.InvWeight
import org.rsmod.api.player.output.UpdateRun
import org.rsmod.api.player.stat.agilityLvl
import org.rsmod.api.player.vars.setActiveMoveSpeed
import org.rsmod.api.player.vars.varMoveSpeed
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.type.obj.ObjTypeList

public class PlayerRunUpdateProcessor @Inject constructor(private val objTypes: ObjTypeList) {
    public fun process(player: Player) {
        player.updateRunWeight()
        player.updateRunEnergy()
    }

    private fun Player.updateRunEnergy() {
        val startRunEnergy = runEnergy

        if (pendingStepCount > 1) {
            decreaseRunEnergy()
        } else {
            restoreRunEnergy()
        }

        if (runEnergy != startRunEnergy) {
            UpdateRun.updateRunEnergy(this, runEnergy)
        }
    }

    private fun Player.decreaseRunEnergy() {
        val weightKg = runWeight / 1000
        val clamped = weightKg.coerceIn(0..64)
        val loss = 67 + (67 * clamped / 64)
        runEnergy = max(0, runEnergy - loss)

        if (runEnergy == 0 && isRunning()) {
            setActiveMoveSpeed(MoveSpeed.Walk)
        }
    }

    private fun Player.isRunning(): Boolean {
        return varMoveSpeed == MoveSpeed.Run || moveSpeed == MoveSpeed.Run
    }

    private fun Player.restoreRunEnergy() {
        // TODO: Var to enable regeneration mod (e.g., 1.3 mod with graceful).
        val recover = 15 + (agilityLvl / 10)
        runEnergy = min(constants.run_max_energy, runEnergy + recover)
    }

    private fun Player.updateRunWeight() {
        if (!pendingRunWeight) {
            return
        }
        val currentGrams = calculateWeightInGrams()
        val previousGrams = runWeight
        runWeight = currentGrams

        val currentKg = currentGrams / 1000
        val previousKg = previousGrams / 1000
        if (previousKg != currentKg) {
            UpdateRun.updateRunWeight(this, kg = currentKg)
        }
    }

    private fun Player.calculateWeightInGrams(): Int {
        return InvWeight.calculateWeightInGrams(this, objTypes)
    }
}
