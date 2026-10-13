@file:Suppress("SpellCheckingInspection")

package org.runerealm.content.skills.firemaking

import org.runerealm.api.type.refs.loc.LocReferences
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.seq.SeqReferences
import org.runerealm.game.type.obj.ObjType

typealias firemaking_objs = FiremakingObjs

object FiremakingObjs : ObjReferences() {
    val tinderbox = find("tinderbox")
    val ashes = find("ashes")
    val logs = find("logs")
    val achey_logs = find("achey_tree_logs")
    val oak_logs = find("oak_logs")
    val willow_logs = find("willow_logs")
    val teak_logs = find("teak_logs")
    val arctic_pine_logs = find("arctic_pine_log")
    val maple_logs = find("maple_logs")
    val mahogany_logs = find("mahogany_logs")
    val yew_logs = find("yew_logs")
    val blisterwood_logs = find("blisterwood_logs")
    val magic_logs = find("magic_logs")
    val redwood_logs = find("redwood_logs")
}

object FiremakingLocs : LocReferences() {
    val fire = find("fire")
}

internal object FiremakingSeqs : SeqReferences() {
    val light = find("human_createfire")
}

/** A burnable log: Firemaking level required and experience, per the OSRS Wiki Firemaking page. */
data class BurnableLog(val type: ObjType, val level: Int, val xp: Double)

val BURNABLE_LOGS: List<BurnableLog> =
    listOf(
        BurnableLog(firemaking_objs.logs, 1, 40.0),
        BurnableLog(firemaking_objs.achey_logs, 1, 40.0),
        BurnableLog(firemaking_objs.oak_logs, 15, 60.0),
        BurnableLog(firemaking_objs.willow_logs, 30, 90.0),
        BurnableLog(firemaking_objs.teak_logs, 35, 105.0),
        BurnableLog(firemaking_objs.arctic_pine_logs, 42, 125.0),
        BurnableLog(firemaking_objs.maple_logs, 45, 135.0),
        BurnableLog(firemaking_objs.mahogany_logs, 50, 157.5),
        BurnableLog(firemaking_objs.yew_logs, 60, 202.5),
        BurnableLog(firemaking_objs.blisterwood_logs, 62, 96.0),
        BurnableLog(firemaking_objs.magic_logs, 75, 303.8),
        BurnableLog(firemaking_objs.redwood_logs, 90, 350.0),
    )
