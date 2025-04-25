package org.rsmod.content.interfaces.prayer.tab.scripts

import jakarta.inject.Inject
import kotlin.math.max
import kotlin.math.min
import org.rsmod.api.config.refs.stats
import org.rsmod.api.config.refs.timers
import org.rsmod.api.config.refs.varbits
import org.rsmod.api.player.bonus.WornBonuses
import org.rsmod.api.player.disablePrayers
import org.rsmod.api.player.output.mes
import org.rsmod.api.player.output.soundSynth
import org.rsmod.api.player.stat.prayerLvl
import org.rsmod.api.player.stat.statSub
import org.rsmod.api.player.vars.intVarBit
import org.rsmod.api.script.advanced.onWearposChange
import org.rsmod.api.script.onPlayerLogIn
import org.rsmod.api.script.onPlayerSoftTimer
import org.rsmod.content.interfaces.prayer.tab.PrayerRepository
import org.rsmod.content.interfaces.prayer.tab.configs.prayer_sounds
import org.rsmod.content.interfaces.prayer.tab.util.drainCounter
import org.rsmod.game.entity.Player
import org.rsmod.plugin.scripts.PluginScript
import org.rsmod.plugin.scripts.ScriptContext

class PrayerDrainScript
@Inject
constructor(private val repo: PrayerRepository, private val bonuses: WornBonuses) : PluginScript() {
    private var Player.drainResistance by intVarBit(varbits.prayer_drain_resistance)

    override fun ScriptContext.startUp() {
        onPlayerSoftTimer(timers.prayer_drain) { player.drainPrayer() }

        onPlayerLogIn { player.updateDrainResistance() }
        onWearposChange { player.updateDrainResistance() }
    }

    private fun Player.drainPrayer() {
        val enabledPrayers = vars[varbits.enabled_prayers]
        if (enabledPrayers == 0) {
            // We favor explicitness and enforce prayer drain timer to be manually cleared instead
            // of implicitly doing so when all prayers are disabled.
            throw IllegalStateException("Prayer drain timer should have been manually cleared.")
        }

        if (prayerLvl == 0) {
            triggerPrayerDepletion()
            return
        }

        val drainEffect = calculateDrainEffect(enabledPrayers)
        drainCounter += drainEffect

        val cappedResistance = max(1, drainResistance)
        val prayerPointCost = (drainCounter - 1) / cappedResistance
        if (prayerPointCost > 0) {
            drainCounter -= prayerPointCost * cappedResistance

            val sub = min(prayerLvl, prayerPointCost)
            statSub(stats.prayer, constant = sub, percent = 0)
        }
    }

    private fun calculateDrainEffect(enabledPrayers: Int): Int {
        var drainEffect = 0
        // Micro-optimization: Using index-based loop to avoid allocating an iterator object.
        val prayers = repo.prayerList
        for (i in prayers.indices) {
            val prayer = prayers[i]
            if (enabledPrayers and (1 shl prayer.id) != 0) {
                drainEffect += prayer.drainEffect
            }
        }
        return drainEffect
    }

    private fun Player.triggerPrayerDepletion() {
        rebuildAppearance()
        mes("You have run out of prayer points, you can recharge at an altar.")
        soundSynth(prayer_sounds.drain)
        disablePrayers()
    }

    private fun Player.updateDrainResistance() {
        val resistance = 60 + (bonuses.prayerBonus(this) * 2)
        drainResistance = resistance
    }
}
