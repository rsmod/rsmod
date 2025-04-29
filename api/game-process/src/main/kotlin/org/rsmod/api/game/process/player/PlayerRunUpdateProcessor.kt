package org.rsmod.api.game.process.player

import jakarta.inject.Inject
import kotlin.math.max
import kotlin.math.min
import org.rsmod.api.config.constants
import org.rsmod.api.config.refs.params
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.inv.weight.InvWeight
import org.rsmod.api.player.output.UpdateRun
import org.rsmod.api.player.stat.agilityLvl
import org.rsmod.api.player.vars.setActiveMoveSpeed
import org.rsmod.api.player.vars.varMoveSpeed
import org.rsmod.game.entity.Player
import org.rsmod.game.movement.MoveSpeed
import org.rsmod.game.type.obj.ObjTypeList
import org.rsmod.game.type.obj.Wearpos

public class PlayerRunUpdateProcessor @Inject constructor(private val objTypes: ObjTypeList) {
    public fun process(player: Player) {
        player.updateRunWeight()
        player.updateRunEnergy()
    }

    /*
     * Note: Ideally, we would handle special cases (e.g., restoration rate mod) in plugins.
     * However, since this runs frequently for all players and is closely tied to the engine
     * loop, we have opted to handle it here for now. This decision may be revisited later.
     */

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
        val weightKg = (runWeight / 1000).coerceIn(0, 64)
        val baseLoss = 60 + (67 * weightKg) / 64

        var loss = baseLoss * (300 - agilityLvl) / 300
        if (hasImprovedStaminaEffect()) {
            loss = (loss * 85) / 100
        } else if (hasStaminaEffect()) {
            loss = (loss * 30) / 100
        }

        runEnergy = max(0, runEnergy - loss)
        if (runEnergy == 0 && isRunning()) {
            setActiveMoveSpeed(MoveSpeed.Walk)
        }
    }

    private fun Player.hasImprovedStaminaEffect(): Boolean {
        return vars[varbits.improved_stamina_passive] == 1
    }

    private fun Player.hasStaminaEffect(): Boolean {
        return vars[varbits.stamina_active] == 1
    }

    private fun Player.isRunning(): Boolean {
        return varMoveSpeed == MoveSpeed.Run || moveSpeed == MoveSpeed.Run
    }

    private fun Player.restoreRunEnergy() {
        if (runEnergy >= constants.run_max_energy) {
            return
        }
        val baseRecover = 15 + (agilityLvl / 10)
        val recover = (restorationRateMod() * baseRecover).toInt()
        runEnergy = min(constants.run_max_energy, runEnergy + recover)
    }

    private fun Player.restorationRateMod(): Double {
        var wornRate = 0
        var pieces = 0

        for (wearpos in gracefulWearpos) {
            val type = objTypes.getOrNull(worn[wearpos.slot]) ?: continue
            val rate = type.paramOrNull(params.graceful_restore_rate) ?: continue
            wornRate += rate
            pieces++
        }

        if (pieces == gracefulWearpos.size) {
            wornRate = FULL_GRACEFUL_RESTORE_RATE
        }

        return 1.0 + (wornRate / 10_000.0)
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

    private companion object {
        private const val FULL_GRACEFUL_RESTORE_RATE = 3000

        private val gracefulWearpos =
            listOf(
                Wearpos.Hat,
                Wearpos.Torso,
                Wearpos.Legs,
                Wearpos.Hands,
                Wearpos.Feet,
                Wearpos.Back,
            )
    }
}
