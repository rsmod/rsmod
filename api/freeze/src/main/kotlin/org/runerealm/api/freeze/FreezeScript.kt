package org.runerealm.api.freeze

import jakarta.inject.Inject
import org.runerealm.api.config.refs.timers
import org.runerealm.api.config.refs.walktriggers
import org.runerealm.api.player.output.clearMapFlag
import org.runerealm.api.player.output.mes
import org.runerealm.api.script.onNpcWalkTrigger
import org.runerealm.api.script.onPlayerSoftTimer
import org.runerealm.api.script.onPlayerWalkTrigger
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList
import org.runerealm.game.type.walktrig.WalkTriggerType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Blocks movement for frozen entities.
 *
 * The movement processors clear an entity's walk trigger right before publishing it, so while the
 * entity is still frozen the trigger is set again to keep blocking future movement.
 */
public class FreezeScript @Inject constructor(private val playerList: PlayerList) : PluginScript() {
    override fun ScriptContext.startup() {
        onPlayerWalkTrigger(walktriggers.frozen) { player.frozenWalk() }
        onPlayerWalkTrigger(walktriggers.pvp_frozen) { player.pvpFrozenWalk() }
        onNpcWalkTrigger(walktriggers.frozen) { npc.frozenWalk() }
        onPlayerSoftTimer(timers.freeze_thaw) { player.thaw() }
    }

    private fun Player.frozenWalk() {
        if (isFrozen()) {
            blockMovement(walktriggers.frozen)
        }
    }

    private fun Player.pvpFrozenWalk() {
        if (!isFrozen()) {
            return
        }
        val source = resolveFreezeSource(playerList)
        if (source == null || !isWithinPvpFreezeRange(source)) {
            endFreeze()
            return
        }
        blockMovement(walktriggers.pvp_frozen)
    }

    private fun Player.blockMovement(trigger: WalkTriggerType) {
        abortRoute()
        clearMapFlag()
        walkTrigger(trigger)
        mes(FROZEN_MESSAGE)
    }

    private fun Npc.frozenWalk() {
        if (isFrozen()) {
            abortRoute()
            walkTrigger(walktriggers.frozen)
        }
    }

    private fun Player.thaw() {
        val remaining = remainingFreezeCycles()
        if (remaining > 0) {
            softTimer(timers.freeze_thaw, remaining)
            return
        }
        clearSoftTimer(timers.freeze_thaw)
        if (walkTrigger.isFreezeTrigger()) {
            clearWalkTrigger()
        }
    }

    private companion object {
        const val FROZEN_MESSAGE = "A magical force stops you from moving."
    }
}
