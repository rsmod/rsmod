package org.runerealm.content.skills.fishing.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.synths
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.fishingLvl
import org.runerealm.api.script.onOpNpc1
import org.runerealm.api.script.onOpNpc3
import org.runerealm.api.stats.levelmod.InvisibleLevels
import org.runerealm.api.stats.xpmod.XpModifiers
import org.runerealm.content.skills.fishing.configs.FishingCatch
import org.runerealm.content.skills.fishing.configs.FishingMethod
import org.runerealm.content.skills.fishing.configs.FishingSpots
import org.runerealm.events.UnboundEvent
import org.runerealm.game.MapClock
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

// TODO:
// - fishing spot movement (every 250-530 ticks, per the OSRS Wiki)
// - angler outfit xp bonus
// - clue bottles, heron pet, random-event loot
class Fishing
@Inject
constructor(
    private val xpMods: XpModifiers,
    private val invisibleLvls: InvisibleLevels,
    private val mapClock: MapClock,
) : PluginScript() {
    override fun ScriptContext.startup() {
        for (spot in FishingSpots.all) {
            spot.op1?.let { method -> onOpNpc1(spot.npc) { fish(it.npc, method, op = 1) } }
            spot.op3?.let { method -> onOpNpc3(spot.npc) { fish(it.npc, method, op = 3) } }
        }
    }

    private fun ProtectedAccess.fish(spot: Npc, method: FishingMethod, op: Int) {
        if (!canFish(method)) {
            return
        }

        if (actionDelay < mapClock) {
            // Fresh attempt: cast out and wait for the first catch roll.
            actionDelay = mapClock + rollDelay()
            skillAnimDelay = actionDelay
            anim(method.anim)
            mes(method.castMessage)
            continueFishing(spot, op)
            return
        }

        if (skillAnimDelay <= mapClock) {
            skillAnimDelay = mapClock + CYCLE_TICKS
            anim(method.anim)
        }

        if (actionDelay == mapClock) {
            actionDelay = mapClock + rollDelay()
            val catch = rollCatch(method)
            if (catch != null) {
                onCatch(spot, method, catch)
            }
        }

        continueFishing(spot, op)
    }

    private fun ProtectedAccess.canFish(method: FishingMethod): Boolean {
        if (method.tool !in inv) {
            mes(method.missingToolMessage)
            return false
        }

        if (player.fishingLvl < method.levelReq) {
            mes(
                "You need a Fishing level of at least ${method.levelReq} to " +
                    "${method.verb} these fish."
            )
            return false
        }

        val bait = method.bait
        if (bait != null && bait !in inv) {
            mes(method.missingBaitMessage ?: "You don't have any bait left.")
            return false
        }

        if (inv.isFull()) {
            mes("You can't carry any more fish.")
            soundSynth(synths.pillory_wrong)
            return false
        }

        return true
    }

    /**
     * Rolls each available catch from the highest level requirement downward, returning the first
     * successful catch (cascading chances, as documented on the OSRS Wiki).
     */
    private fun ProtectedAccess.rollCatch(method: FishingMethod): FishingCatch? {
        val level = player.fishingLvl
        for (catch in method.catches) {
            if (level < catch.level) {
                continue
            }
            if (statRandom(stats.fishing, catch.low, catch.high, invisibleLvls)) {
                return catch
            }
        }
        return null
    }

    private fun ProtectedAccess.onCatch(spot: Npc, method: FishingMethod, catch: FishingCatch) {
        val bait = method.bait
        if (bait != null) {
            invDel(inv, bait, count = 1)
        }
        invAdd(inv, catch.fish)
        spam(catch.message)
        statAdvance(stats.fishing, catch.xp * xpMods.get(player, stats.fishing))
        publish(CatchFish(player, spot, catch))
    }

    private fun ProtectedAccess.continueFishing(spot: Npc, op: Int) {
        when (op) {
            1 -> opNpc1(spot)
            3 -> opNpc3(spot)
            else -> throw IllegalArgumentException("Unsupported fishing op: $op")
        }
    }

    /**
     * Catch rolls happen every 5 ticks, with a 1/5 chance of the interval being extended to 6
     * ticks. (Mod Ash, via the OSRS Wiki "Raw pike" page.)
     */
    private fun ProtectedAccess.rollDelay(): Int =
        if (random.randomBoolean(EXTENDED_CYCLE_CHANCE)) CYCLE_TICKS + 1 else CYCLE_TICKS

    data class CatchFish(val player: Player, val spot: Npc, val catch: FishingCatch) : UnboundEvent

    private companion object {
        const val CYCLE_TICKS = 5
        const val EXTENDED_CYCLE_CHANCE = 5
    }
}
