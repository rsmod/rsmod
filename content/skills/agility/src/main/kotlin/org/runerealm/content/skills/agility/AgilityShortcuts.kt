package org.runerealm.content.skills.agility

import jakarta.inject.Inject
import org.runerealm.api.config.refs.stats
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.stat.statBase
import org.runerealm.api.script.onOpLoc1
import org.runerealm.api.type.refs.seq.SeqReferences
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.stat.StatTypeList
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/** Agility shortcuts listed in [SHORTCUTS]. */
class AgilityShortcuts
@Inject
constructor(private val locTypes: LocTypeList, private val statTypes: StatTypeList) :
    PluginScript() {
    override fun ScriptContext.startup() {
        val byLoc =
            SHORTCUTS.flatMap { s -> s.locs.map { it to s } }.groupBy({ it.first }, { it.second })
        for (type in locTypes.values) {
            val shortcuts = byLoc[type.internalName] ?: continue
            onOpLoc1(type) { use(it.loc, shortcuts) }
        }
    }

    private suspend fun ProtectedAccess.use(loc: BoundLocInfo, candidates: List<Shortcut>) {
        // The same loc type can be used by more than one shortcut; pick the closest one.
        val shortcut = candidates.minByOrNull { it.distanceTo(loc.coords) } ?: return
        if (player.statBase(stats.agility) < shortcut.level) {
            mes("You need an Agility level of ${shortcut.level} to negotiate this obstacle.")
            return
        }
        val requirement = shortcut.otherRequirement
        if (requirement != null) {
            val (statName, level) = requirement
            val stat = statTypes.values.single { it.internalName == statName }
            if (player.statBase(stat) < level) {
                mes("You need a ${stat.displayName} level of $level to negotiate this obstacle.")
                return
            }
        }
        val from = player.coords
        val nearA = from.distance(shortcut.sideA) <= from.distance(shortcut.sideB)
        if (shortcut.oneWay && !nearA) {
            mes("You can't use this shortcut from this side.")
            return
        }
        val dest = if (nearA) shortcut.sideB else shortcut.sideA
        cross(shortcut, dest)
        if (shortcut.xp > 0) {
            statAdvance(stats.agility, shortcut.xp)
        }
    }

    private suspend fun ProtectedAccess.cross(shortcut: Shortcut, dest: CoordGrid) {
        val start = player.coords
        val face = faceTowards(start, dest)
        // Longer crossings take one tick per tile travelled.
        val ticks = start.distance(dest).coerceIn(1, MAX_CROSSING_TICKS)
        when (shortcut.style) {
            ShortcutStyle.ClimbOver -> {
                anim(shortcut_seqs.climb_over)
                exactMove(start, dest, delay1 = 0, delay2 = 60, dir = face)
                delay(2)
            }
            ShortcutStyle.FenceJump -> {
                anim(shortcut_seqs.fence_jump)
                exactMove(start, dest, delay1 = 0, delay2 = 30, dir = face)
                delay(1)
            }
            ShortcutStyle.SteppingStones -> {
                anim(shortcut_seqs.jump)
                exactMove(start, dest, delay1 = 0, delay2 = ticks * 30, dir = face)
                delay(ticks)
            }
            ShortcutStyle.Scramble -> {
                anim(shortcut_seqs.scramble)
                exactMove(start, dest, delay1 = 0, delay2 = ticks * 30, dir = face)
                delay(ticks)
            }
            ShortcutStyle.LogBalance -> {
                anim(shortcut_seqs.log_balance)
                exactMove(start, dest, delay1 = 0, delay2 = ticks * 30, dir = face)
                delay(ticks)
            }
            ShortcutStyle.Tunnel -> {
                anim(shortcut_seqs.tunnel_enter)
                delay(2)
                anim(shortcut_seqs.tunnel_exit)
                delay(1)
            }
            ShortcutStyle.Pipe -> {
                anim(shortcut_seqs.pipe)
                exactMove(start, dest, delay1 = 0, delay2 = ticks * 30, dir = face)
                delay(ticks)
            }
            ShortcutStyle.Squeeze -> {
                anim(shortcut_seqs.squeeze)
                delay(2)
                anim(shortcut_seqs.unsqueeze)
            }
        }
        telejump(dest)
        resetAnim()
    }

    private fun faceTowards(from: CoordGrid, to: CoordGrid): Int {
        val dx = to.x - from.x
        val dz = to.z - from.z
        return when {
            kotlin.math.abs(dx) >= kotlin.math.abs(dz) && dx > 0 -> FACE_EAST
            kotlin.math.abs(dx) >= kotlin.math.abs(dz) -> FACE_WEST
            dz > 0 -> FACE_NORTH
            else -> FACE_SOUTH
        }
    }

    private fun Shortcut.distanceTo(coords: CoordGrid): Int =
        minOf(coords.distance(sideA), coords.distance(sideB))

    private fun CoordGrid.distance(other: CoordGrid): Int =
        if (level != other.level) Int.MAX_VALUE
        else maxOf(kotlin.math.abs(x - other.x), kotlin.math.abs(z - other.z))

    private companion object {
        const val MAX_CROSSING_TICKS = 12
        const val FACE_SOUTH = 0
        const val FACE_WEST = 512
        const val FACE_NORTH = 1024
        const val FACE_EAST = 1536
    }
}

internal object shortcut_seqs : SeqReferences() {
    val climb_over = find("human_walk_crumbledwall")
    val fence_jump = find("human_lowwall")
    val jump = find("human_spot_jump")
    val scramble = find("human_reachforladder")
    val tunnel_enter = find("agilty_shortcut_enter_hole")
    val tunnel_exit = find("agilty_shortcut_exit_hole")
    val pipe = find("human_doublepipesqueeze")
    val squeeze = find("human_pipesqueeze")
    val unsqueeze = find("human_pipeunsqueeze")
    val log_balance = find("human_walk_logbalance")
}
