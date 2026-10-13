package org.runerealm.content.skills.magic.teleports

import jakarta.inject.Inject
import org.runerealm.api.area.checker.WildernessArea
import org.runerealm.api.config.refs.varps
import org.runerealm.api.player.output.clearMapFlag
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.protect.ProtectedAccessLauncher
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.script.onIfOverlayButton
import org.runerealm.api.script.onPlayerQueueWithArgs
import org.runerealm.api.type.refs.comp.ComponentReferences
import org.runerealm.api.type.refs.queue.QueueReferences
import org.runerealm.api.type.refs.seq.SeqReferences
import org.runerealm.api.type.refs.spot.SpotanimReferences
import org.runerealm.api.type.refs.synth.SynthReferences
import org.runerealm.game.entity.Player
import org.runerealm.map.CoordGrid
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * [Lumbridge Home Teleport](https://oldschool.runescape.wiki/w/Lumbridge_Home_Teleport): no runes,
 * no experience and a 24-tick cast.
 *
 * Per the wiki ([Home Teleport](https://oldschool.runescape.wiki/w/Home_Teleport)):
 * - It "can be interrupted at any time by most actions, including combat". Each animation stage is
 *   therefore scheduled as a **weak queue**: walking, clicking another entity, or opening an
 *   interface clears weak queues (`clearpendingaction`), which cancels the cast. The player is
 *   never delayed, so these inputs are accepted while casting.
 * - It "only can be cast 10 seconds or more after combat has ended (similar to logging out)".
 * - Like most teleport spells it cannot be cast beyond level 20 Wilderness.
 *
 * This server intentionally has **no** 30-minute cooldown.
 */
class HomeTeleport @Inject constructor(private val protectedAccess: ProtectedAccessLauncher) :
    PluginScript() {
    override fun ScriptContext.startup() {
        onIfOverlayButton(home_teleport_components.standard) {
            protectedAccess.launch(player) { homeTeleport() }
        }
        onPlayerQueueWithArgs<HomeTeleportCast>(home_teleport_queues.cast) {
            homeTeleportStage(it.args)
        }
    }
}

/** State carried between the weak-queued animation stages. */
internal data class HomeTeleportCast(val stage: Int, val start: Int)

internal fun ProtectedAccess.homeTeleport() {
    if (player.inRecentCombat(mapClock)) {
        mes(RECENT_COMBAT_MESSAGE)
        return
    }
    if (WildernessArea.level(player.coords) > HOME_TELEPORT_WILDERNESS_LIMIT) {
        mes("A mysterious force blocks your teleport spell!")
        mes("You can't use this teleport after level $HOME_TELEPORT_WILDERNESS_LIMIT wilderness.")
        return
    }
    // Casting stops the player where they stand; any other pending weak action is replaced.
    player.abortRoute()
    player.clearMapFlag()
    clearWeakQueue(home_teleport_queues.cast)
    playStage(HomeTeleportCast(stage = 0, start = mapClock))
}

internal fun ProtectedAccess.homeTeleportStage(cast: HomeTeleportCast) {
    if (player.attackedSince(cast.start)) {
        resetAnim()
        spotanim(null)
        return
    }
    if (cast.stage >= HOME_TELEPORT_STAGES.size) {
        telejump(LUMBRIDGE_HOME)
        resetAnim()
        return
    }
    playStage(cast)
}

private fun ProtectedAccess.playStage(cast: HomeTeleportCast) {
    val stage = HOME_TELEPORT_STAGES[cast.stage]
    stage.synth?.let(::soundSynth)
    anim(stage.seq)
    spotanim(stage.spot)
    // A queue counts down on the cycle it is added in, so `ticks + 1` makes the next stage start
    // exactly `ticks` cycles after this one.
    weakQueue(home_teleport_queues.cast, stage.ticks + 1, cast.copy(stage = cast.stage + 1))
}

/** The wiki: castable "10 seconds or more after combat has ended (similar to logging out)". */
private fun Player.inRecentCombat(mapClock: Int): Boolean =
    lastCombat != 0 && lastCombat + HOME_TELEPORT_COMBAT_DELAY > mapClock

private fun Player.attackedSince(start: Int): Boolean = lastCombat != 0 && lastCombat >= start

private var Player.lastCombat by intVarp(varps.lastcombat)

/** 10 seconds, matching the logout restriction. */
const val HOME_TELEPORT_COMBAT_DELAY = 16

const val HOME_TELEPORT_WILDERNESS_LIMIT = 20

const val RECENT_COMBAT_MESSAGE = "You can't use this teleport after recent combat."

/** Destination per the wiki infobox map: 3221, 3218. */
val LUMBRIDGE_HOME = CoordGrid(3221, 3218, 0)

private class HomeTeleportStage(
    val seq: org.runerealm.game.type.seq.SeqType,
    val spot: org.runerealm.game.type.spot.SpotanimType,
    val synth: org.runerealm.game.type.synth.SynthType?,
    val ticks: Int,
)

/** Five animation stages totalling the wiki's 24-tick cast. */
private val HOME_TELEPORT_STAGES =
    listOf(
        HomeTeleportStage(home_seqs.chalk, home_spots.chalk, home_synths.chalk, 7),
        HomeTeleportStage(home_seqs.sit, home_spots.chalk_static, home_synths.sitdown, 5),
        HomeTeleportStage(home_seqs.book, home_spots.book, home_synths.book, 4),
        HomeTeleportStage(home_seqs.recite, home_spots.portal, home_synths.portal, 4),
        HomeTeleportStage(home_seqs.teleport, home_spots.teleport, null, 4),
    )

typealias home_teleport_components = HomeTeleportComponents

object HomeTeleportComponents : ComponentReferences() {
    val standard = find("magic_spellbook:teleport_home_standard")
}

internal object home_teleport_queues : QueueReferences() {
    val cast = find("home_teleport")
}

internal object home_seqs : SeqReferences() {
    val chalk = find("aide_drawing_chalk_circle")
    val sit = find("aide_sitting_down_crosslegged")
    val book = find("aide_player_getting_book")
    val recite = find("aide_reciting_incantation")
    val teleport = find("aide_player_teleporting")
}

internal object home_spots : SpotanimReferences() {
    val chalk = find("aide_chalk_circle")
    val chalk_static = find("aide_chalk_circle_static")
    val book = find("aide_player_book_get")
    val portal = find("aide_player_book_portal")
    val teleport = find("aide_player_teleport")
}

internal object home_synths : SynthReferences() {
    val chalk = find("aide_teleport_chalk")
    val sitdown = find("aide_teleport_sitdown")
    val book = find("aide_teleport_book")
    val portal = find("aide_teleport_portal")
}
