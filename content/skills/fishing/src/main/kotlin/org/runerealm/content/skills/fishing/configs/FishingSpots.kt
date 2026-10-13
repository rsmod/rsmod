package org.runerealm.content.skills.fishing.configs

import org.runerealm.api.config.refs.npcs
import org.runerealm.game.type.npc.NpcType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.seq.SeqType

/**
 * A single fish that can be caught with a [FishingMethod].
 *
 * [low] and [high] are the skilling success function parameters (out of 256) as documented on the
 * OSRS Wiki (https://oldschool.runescape.wiki/w/Catch_rate).
 */
data class FishingCatch(
    val fish: ObjType,
    val level: Int,
    val xp: Double,
    val low: Int,
    val high: Int,
    val message: String,
)

/**
 * A fishing method available on a spot, e.g. "Net" or "Bait".
 *
 * [catches] must be ordered from the highest to the lowest level requirement: the highest-level
 * fish the player can catch is rolled first, cascading down on failure.
 */
data class FishingMethod(
    val tool: ObjType,
    val bait: ObjType?,
    val anim: SeqType,
    val verb: String,
    val castMessage: String,
    val missingToolMessage: String,
    val missingBaitMessage: String?,
    val catches: List<FishingCatch>,
) {
    val levelReq: Int
        get() = catches.minOf { it.level }

    init {
        check(catches.isNotEmpty()) { "Fishing method must have at least one catch." }
        check(catches.zipWithNext().all { (a, b) -> a.level >= b.level }) {
            "Fishing catches must be ordered from highest to lowest level: $catches"
        }
    }
}

data class FishingSpot(val npc: NpcType, val op1: FishingMethod?, val op3: FishingMethod?)

object FishingMethods {
    // TODO: Verify cast/missing tool/missing bait messages; the OSRS Wiki does not document them.
    val small_net =
        FishingMethod(
            tool = fishing_objs.small_fishing_net,
            bait = null,
            anim = fishing_seqs.small_net,
            verb = "net",
            castMessage = "You cast out your net...",
            missingToolMessage = "You need a net to catch these fish.",
            missingBaitMessage = null,
            catches =
                listOf(
                    FishingCatch(
                        fish = fishing_objs.raw_anchovies,
                        level = 15,
                        xp = 40.0,
                        low = 24,
                        high = 128,
                        message = "You catch some raw anchovies.",
                    ),
                    FishingCatch(
                        fish = fishing_objs.raw_shrimps,
                        level = 1,
                        xp = 10.0,
                        low = 48,
                        high = 256,
                        message = "You catch some raw shrimps.",
                    ),
                ),
        )

    val sea_bait =
        FishingMethod(
            tool = fishing_objs.fishing_rod,
            bait = fishing_objs.fishing_bait,
            anim = fishing_seqs.rod_casting,
            verb = "bait",
            castMessage = "You cast out your line...",
            missingToolMessage = "You need a Fishing Rod to bait these fish.",
            missingBaitMessage = "You don't have any bait left.",
            catches =
                listOf(
                    FishingCatch(
                        fish = fishing_objs.raw_herring,
                        level = 10,
                        xp = 30.0,
                        low = 24,
                        high = 128,
                        message = "You catch a raw herring.",
                    ),
                    FishingCatch(
                        fish = fishing_objs.raw_sardine,
                        level = 5,
                        xp = 20.0,
                        low = 32,
                        high = 192,
                        message = "You catch a raw sardine.",
                    ),
                ),
        )

    val lure =
        FishingMethod(
            tool = fishing_objs.fly_fishing_rod,
            bait = fishing_objs.feather,
            anim = fishing_seqs.rod_casting,
            verb = "lure",
            castMessage = "You cast out your line...",
            missingToolMessage = "You need a fly fishing rod to lure these fish.",
            missingBaitMessage = "You don't have any feathers left.",
            catches =
                listOf(
                    // The OSRS Wiki notes that these messages are missing their trailing period.
                    FishingCatch(
                        fish = fishing_objs.raw_salmon,
                        level = 30,
                        xp = 70.0,
                        low = 16,
                        high = 96,
                        message = "You catch a raw salmon",
                    ),
                    FishingCatch(
                        fish = fishing_objs.raw_trout,
                        level = 20,
                        xp = 50.0,
                        low = 32,
                        high = 192,
                        message = "You catch a raw trout",
                    ),
                ),
        )

    val river_bait =
        FishingMethod(
            tool = fishing_objs.fishing_rod,
            bait = fishing_objs.fishing_bait,
            anim = fishing_seqs.rod_casting,
            verb = "bait",
            castMessage = "You cast out your line...",
            missingToolMessage = "You need a Fishing Rod to bait these fish.",
            missingBaitMessage = "You don't have any bait left.",
            catches =
                listOf(
                    // TODO: Verify message; the OSRS Wiki does not document the pike catch message.
                    FishingCatch(
                        fish = fishing_objs.raw_pike,
                        level = 25,
                        xp = 60.0,
                        low = 16,
                        high = 96,
                        message = "You catch a pike.",
                    )
                ),
        )
}

object FishingSpots {
    val net_bait_lumbridge =
        FishingSpot(
            npc = npcs.fishing_spot_1530,
            op1 = FishingMethods.small_net,
            op3 = FishingMethods.sea_bait,
        )

    val lure_bait_lumbridge =
        FishingSpot(
            npc = npcs.rod_fishing_spot_1527,
            op1 = FishingMethods.lure,
            op3 = FishingMethods.river_bait,
        )

    val all: List<FishingSpot> = listOf(net_bait_lumbridge, lure_bait_lumbridge)
}
