package org.runerealm.content.skills.firemaking

import jakarta.inject.Inject
import org.runerealm.api.config.refs.stats
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.statBase
import org.runerealm.api.repo.loc.LocRepository
import org.runerealm.api.repo.obj.ObjRepository
import org.runerealm.api.script.onOpHeldU
import org.runerealm.api.script.onOpObj4
import org.runerealm.game.MapClock
import org.runerealm.game.loc.LocAngle
import org.runerealm.game.loc.LocShape
import org.runerealm.game.obj.Obj
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Lighting logs with a tinderbox, per the OSRS Wiki
 * ([Firemaking](https://oldschool.runescape.wiki/w/Firemaking)):
 * - Using a tinderbox and logs together drops the logs and lights them; logs on the ground can be
 *   lit with their "Light" option.
 * - The chance to light is 65/256 at level 1 rising to 513/256 at level 99 (always succeeds from
 *   level 43).
 * - Once lit, the player steps west if possible, otherwise east, south, then north.
 * - A fire burns for an unpredictable time and leaves ashes behind.
 */
class Firemaking
@Inject
constructor(
    private val objRepo: ObjRepository,
    private val locRepo: LocRepository,
    private val clock: MapClock,
) : PluginScript() {
    override fun ScriptContext.startup() {
        for (log in BURNABLE_LOGS) {
            onOpHeldU(firemaking_objs.tinderbox, log.type) { lightHeldLog(log) }
            onOpObj4(log.type) { lightGroundLog(it.obj, log) }
        }
    }

    private suspend fun ProtectedAccess.lightHeldLog(log: BurnableLog) {
        if (!canLight(log)) {
            return
        }
        val slot = player.inv.indexOfFirst { it?.id == log.type.id }
        if (slot == -1) {
            return
        }
        invDel(player.inv, log.type, count = 1, slot = slot)
        val dropped = objRepo.add(log.type, player.coords, GROUND_LOG_DURATION, player)
        light(dropped, log)
    }

    private suspend fun ProtectedAccess.lightGroundLog(obj: Obj, log: BurnableLog) {
        if (!canLight(log)) {
            return
        }
        if (objs.tinderbox !in player.inv) {
            mes("You need a tinderbox to light a fire.")
            return
        }
        light(obj, log)
    }

    private fun ProtectedAccess.canLight(log: BurnableLog): Boolean {
        if (player.statBase(stats.firemaking) < log.level) {
            mes("You need a Firemaking level of ${log.level} to burn these logs.")
            return false
        }
        if (locRepo.findAll(player.coords).any()) {
            mes("You can't light a fire here.")
            return false
        }
        return true
    }

    private suspend fun ProtectedAccess.light(obj: Obj, log: BurnableLog) {
        mes("You attempt to light the logs.")
        while (true) {
            anim(FiremakingSeqs.light)
            delay(LIGHT_ATTEMPT_DELAY)
            if (statRandom(stats.firemaking, LOW_CHANCE, HIGH_CHANCE, invisibleBoost = 0)) {
                break
            }
        }
        // Someone may have taken the logs while we were trying to light them.
        if (!objRepo.del(obj)) {
            resetAnim()
            return
        }
        val coords = obj.coords
        val burnTime = random.of(FIRE_MIN_DURATION, FIRE_MAX_DURATION)
        locRepo.add(
            coords,
            FiremakingLocs.fire,
            burnTime,
            LocAngle.West,
            LocShape.CentrepieceStraight,
        )
        objRepo.addDelayed(Obj.fromServer(clock, coords, objs.ashes, 1), burnTime, ASHES_DURATION)

        statAdvance(stats.firemaking, log.xp)
        resetAnim()
        mes("The fire catches and the logs begin to burn.")
        stepAwayFrom(coords)
    }

    private fun ProtectedAccess.stepAwayFrom(fire: CoordGrid) {
        val step =
            STEP_ORDER.map { (dx, dz) -> fire.translate(dx, dz) }
                .firstOrNull { lineOfWalk(fire, it) } ?: return
        walk(step)
    }

    private companion object {
        val objs = firemaking_objs

        /** West, then east, then south, then north. */
        val STEP_ORDER = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        const val LOW_CHANCE = 65
        const val HIGH_CHANCE = 513
        const val LIGHT_ATTEMPT_DELAY = 4
        const val GROUND_LOG_DURATION = 200
        const val FIRE_MIN_DURATION = 100
        const val FIRE_MAX_DURATION = 199
        const val ASHES_DURATION = 200
    }
}
