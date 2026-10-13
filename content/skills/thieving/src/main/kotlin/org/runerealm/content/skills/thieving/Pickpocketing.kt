package org.runerealm.content.skills.thieving

import jakarta.inject.Inject
import org.runerealm.api.config.refs.content
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.spotanims
import org.runerealm.api.config.refs.stats
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.random.GameRandom
import org.runerealm.api.repo.obj.ObjRepository
import org.runerealm.api.script.onOpNpc3
import org.runerealm.api.type.refs.npc.NpcReferences
import org.runerealm.api.type.refs.obj.ObjReferences
import org.runerealm.api.type.refs.seq.SeqReferences
import org.runerealm.game.entity.Npc
import org.runerealm.game.hit.HitType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

/**
 * Pickpocketing the NPCs found around Lumbridge, following
 * [Pickpocketing](https://oldschool.runescape.wiki/w/Thieving#Pickpocketing):
 * - A successful attempt takes two ticks and gives the target's loot and experience.
 * - A failed attempt gets the player punched for the target's damage and stunned, which stops them
 *   from pickpocketing again for eight ticks
 *   ([Stun](https://oldschool.runescape.wiki/w/Stun_(status))).
 *
 * Success chances use each target's "Skilling success chart" `low`/`high` values from the wiki.
 */
class Pickpocketing
@Inject
constructor(private val objRepo: ObjRepository, private val random: GameRandom) : PluginScript() {
    override fun ScriptContext.startup() {
        onOpNpc3(content.person) { pickpocket(it.npc, PickpocketTarget.Person) }
        for (farmer in thieving_npcs.farmers) {
            onOpNpc3(farmer) { pickpocket(it.npc, PickpocketTarget.Farmer) }
        }
        onOpNpc3(thieving_npcs.sigmund) { pickpocket(it.npc, PickpocketTarget.Sigmund) }
    }

    private suspend fun ProtectedAccess.pickpocket(npc: Npc, target: PickpocketTarget) {
        val possessive = target.possessive(npc)
        if (statBase(stats.thieving) < target.level) {
            mes("You need a Thieving level of ${target.level} to pick $possessive pocket.")
            return
        }
        if (target.loot.isNotEmpty() && inv.isFull()) {
            mes("You don't have enough inventory space to do that.")
            return
        }
        faceEntitySquare(npc)
        mes("You attempt to pick $possessive pocket.")
        anim(thieving_seqs.human_pickpocket)
        delay(2)

        val success = statRandom(stats.thieving, target.low, target.high, invisibleBoost = 0)
        if (!success) {
            fail(npc, target, possessive)
            return
        }
        mes("You pick $possessive pocket.")
        if (target.xp > 0.0) {
            statAdvance(stats.thieving, target.xp)
        }
        val loot = target.rollLoot()
        if (loot != null) {
            invAddOrDrop(objRepo, loot.type, loot.count)
        }
    }

    private suspend fun ProtectedAccess.fail(
        npc: Npc,
        target: PickpocketTarget,
        possessive: String,
    ) {
        mes("You fail to pick $possessive pocket.")
        npcPlayerFaceClose(npc)
        npc.anim(thieving_seqs.human_unarmedpunch)
        npc.say("What do you think you're doing?")
        takeInstantHit(HitType.Typeless, target.damage)
        spotanim(spotanims.stunned, height = 100)
        mes("You have been stunned.")
        delay(STUN_TICKS)
    }

    private fun PickpocketTarget.rollLoot(): Loot? {
        if (loot.isEmpty()) {
            return null
        }
        var roll = random.of(0, loot.sumOf { it.weight } - 1)
        for (entry in loot) {
            if (roll < entry.weight) {
                return entry
            }
            roll -= entry.weight
        }
        return null
    }

    private companion object {
        /** Ticks a failed pickpocket stops the player from acting (4.8 seconds). */
        const val STUN_TICKS = 8
    }
}

private class Loot(val type: ObjType, val count: Int, val weight: Int = 1)

private enum class PickpocketTarget(
    /** The name used in messages, or `null` to use the npc's own (lowercase) name. */
    private val noun: String?,
    val level: Int,
    val xp: Double,
    val low: Int,
    val high: Int,
    val damage: Int,
    lootProvider: () -> List<Loot>,
) {
    /**
     * [Man](https://oldschool.runescape.wiki/w/Man) and
     * [Woman](https://oldschool.runescape.wiki/w/Woman).
     */
    Person(
        noun = null,
        level = 1,
        xp = 8.0,
        low = 180,
        high = 240,
        damage = 1,
        lootProvider = { listOf(Loot(objs.coins, 3)) },
    ),
    /** [Farmer](https://oldschool.runescape.wiki/w/Farmer): 9 coins (123/128) or a potato seed. */
    Farmer(
        noun = "the farmer",
        level = 10,
        xp = 14.5,
        low = 150,
        high = 240,
        damage = 1,
        lootProvider = {
            listOf(
                Loot(objs.coins, 9, weight = 123),
                Loot(thieving_objs.potato_seed, 1, weight = 5),
            )
        },
    ),
    /**
     * [Sigmund](https://oldschool.runescape.wiki/w/Sigmund) only carries a key during The Lost
     * Tribe, which is not implemented, so there is nothing to steal and no experience.
     */
    Sigmund(
        noun = "Sigmund",
        level = 13,
        xp = 0.0,
        low = 255,
        high = 255,
        damage = 1,
        lootProvider = { emptyList() },
    );

    val loot: List<Loot> by lazy(lootProvider)

    fun possessive(npc: Npc): String = "${noun ?: "the ${npc.name.lowercase()}"}'s"
}

internal object thieving_npcs : NpcReferences() {
    val farmers =
        listOf(
            find("farmer1"),
            find("farmer2"),
            find("farmer3"),
            find("farmer4"),
            find("farmer1_f"),
            find("farmer2_f"),
            find("farmer3_f"),
        )
    val sigmund = find("lost_tribe_sigmund")
}

internal object thieving_objs : ObjReferences() {
    val potato_seed = find("potato_seed")
}

internal object thieving_seqs : SeqReferences() {
    val human_pickpocket = find("human_pickpocket")
    val human_unarmedpunch = find("human_unarmedpunch")
}
