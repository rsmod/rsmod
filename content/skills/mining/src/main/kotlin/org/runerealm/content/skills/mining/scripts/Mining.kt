package org.runerealm.content.skills.mining.scripts

import jakarta.inject.Inject
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.synths
import org.runerealm.api.player.front
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.righthand
import org.runerealm.api.player.stat.miningLvl
import org.runerealm.api.random.GameRandom
import org.runerealm.api.repo.loc.LocRepository
import org.runerealm.api.script.onOpLoc1
import org.runerealm.api.script.onOpLoc3
import org.runerealm.api.stats.levelmod.InvisibleLevels
import org.runerealm.api.stats.xpmod.XpModifiers
import org.runerealm.content.skills.mining.configs.MineRate
import org.runerealm.content.skills.mining.configs.MiningRock
import org.runerealm.content.skills.mining.configs.MiningRocks
import org.runerealm.content.skills.mining.configs.Pickaxe
import org.runerealm.content.skills.mining.configs.Pickaxes
import org.runerealm.content.skills.mining.configs.RockKind
import org.runerealm.content.skills.mining.configs.mining_locs
import org.runerealm.content.skills.mining.configs.mining_objs
import org.runerealm.events.UnboundEvent
import org.runerealm.game.MapClock
import org.runerealm.game.entity.Player
import org.runerealm.game.loc.BoundLocInfo
import org.runerealm.game.type.loc.LocType
import org.runerealm.game.type.loc.LocTypeList
import org.runerealm.game.type.loc.UnpackedLocType
import org.runerealm.game.type.obj.ObjType
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.plugin.scripts.PluginScript
import org.runerealm.plugin.scripts.ScriptContext

// TODO:
// - mining gloves, varrock armour, mining cape and celestial ring effects
// - clue geodes and other tertiary drops
// - infernal pickaxe ore smelting and crystal pickaxe charges
class Mining
@Inject
constructor(
    private val objTypes: ObjTypeList,
    private val locTypes: LocTypeList,
    private val locRepo: LocRepository,
    private val xpMods: XpModifiers,
    private val invisibleLvls: InvisibleLevels,
    private val mapClock: MapClock,
) : PluginScript() {
    /** Mineable rocks keyed by loc id. */
    private val rocks = HashMap<Int, ResolvedRock>()

    /** Depleted rocks keyed by loc id. */
    private val depleted = HashSet<Int>()

    override fun ScriptContext.startup() {
        resolveRocks()
        for (resolved in rocks.values) {
            val type = resolved.type
            onOpLoc1(type) { attempt(it.loc, resolved) }
            onOpLoc3(type) { mine(it.loc, resolved) }
        }
        for (id in depleted) {
            val type = locTypes.getValue(id)
            onOpLoc1(type) { mes("There is no ore currently available in this rock.") }
        }
    }

    /**
     * Resolves every mineable loc into its [MiningRock] definition and depleted loc. Rocks are
     * matched by cache category, so every rock of a given ore in the world shares the same
     * behaviour. Depleted variants are matched by model, the same way the game recolours a single
     * rock model for each ore.
     */
    private fun resolveRocks() {
        val emptyByModel = HashMap<Int, LocType>()
        for (empty in depletedRockTypes()) {
            val type = locTypes.getValue(empty.id)
            depleted += type.id
            if (empty.id == mining_locs.amethyst_empty.id) {
                continue
            }
            type.models.firstOrNull()?.let { emptyByModel.putIfAbsent(it, empty) }
        }

        val byCategory = HashMap<Int, MiningRock>()
        val byLoc = HashMap<Int, MiningRock>()
        for (rock in MiningRocks.all) {
            rock.categories.forEach { byCategory[it.id] = rock }
            rock.locs.forEach { byLoc[it.id] = rock }
        }

        for (type in locTypes.values) {
            if (!type.op.getOrNull(0).equals(MINE_OP, ignoreCase = true)) {
                continue
            }
            val rock = byLoc[type.id] ?: byCategory[type.category] ?: continue
            val empty =
                when (rock.kind) {
                    RockKind.Essence -> null
                    RockKind.Amethyst -> mining_locs.amethyst_empty
                    else -> type.models.firstOrNull()?.let { emptyByModel[it] } ?: continue
                }
            rocks[type.id] = ResolvedRock(type, rock, empty)
        }
    }

    private fun depletedRockTypes(): List<LocType> =
        listOf(
            mining_locs.rocks1,
            mining_locs.rocks2,
            mining_locs.rocks3,
            mining_locs.prif_rocks_empty,
            mining_locs.vein_depleted_l,
            mining_locs.vein_depleted_mid,
            mining_locs.vein_depleted_r,
            mining_locs.vein_depleted_single,
            mining_locs.amethyst_empty,
        )

    private fun ProtectedAccess.canMine(rock: MiningRock): Pickaxe? {
        if (player.miningLvl < rock.level) {
            mes("You need a Mining level of ${rock.level} to mine this rock.")
            return null
        }

        val pickaxe = findPickaxe(player)
        if (pickaxe == null) {
            mes("You need a pickaxe to mine this rock.")
            mes("You do not have a pickaxe which you have the Mining level to use.")
            return null
        }

        if (inv.isFull()) {
            mes("Your inventory is too full to hold any more ${rock.oreName}.")
            soundSynth(synths.pillory_wrong)
            return null
        }
        return pickaxe
    }

    private fun ProtectedAccess.attempt(loc: BoundLocInfo, resolved: ResolvedRock) {
        val pickaxe = canMine(resolved.rock) ?: return
        anim(pickaxe.anim)
        skillAnimDelay = mapClock + ANIM_CYCLE
        spam("You swing your pick at the rock.")
        // Re-clicking the rock does not restart the current roll cycle.
        if (actionDelay < mapClock) {
            actionDelay = mapClock + pickaxe.rollDelay(random)
        }
        opLoc3(loc)
    }

    private fun ProtectedAccess.mine(loc: BoundLocInfo, resolved: ResolvedRock) {
        val rock = resolved.rock
        val pickaxe = canMine(rock) ?: return

        if (skillAnimDelay <= mapClock) {
            skillAnimDelay = mapClock + ANIM_CYCLE
            anim(pickaxe.anim)
        }

        if (actionDelay < mapClock) {
            actionDelay = mapClock + pickaxe.rollDelay(random)
        } else if (actionDelay == mapClock) {
            actionDelay = mapClock + pickaxe.rollDelay(random)
            val depleted = roll(loc, resolved)
            if (depleted) {
                return
            }
        }
        opLoc3(loc)
    }

    /** Rolls a single mining attempt. Returns `true` if the rock depleted. */
    private fun ProtectedAccess.roll(loc: BoundLocInfo, resolved: ResolvedRock): Boolean {
        val rock = resolved.rock

        if (rock.kind != RockKind.Essence && rollRandomGem()) {
            return false
        }

        val mined =
            when (rock.kind) {
                RockKind.Essence -> essence()
                RockKind.Gem -> gemRock(rock) ?: return false
                else -> cascade(rock) ?: return false
            }

        val xp = mined.second * xpMods.get(player, stats.mining)
        invAdd(inv, mined.first)
        statAdvance(stats.mining, xp)
        if (rock.kind == RockKind.Gem) {
            val name = objTypes[mined.first].name.removePrefix("Uncut ").lowercase()
            spam("You just mined ${article(name)} $name!")
        } else if (rock.kind != RockKind.Essence) {
            spam(rock.successMessage)
        }
        publish(MinedRock(player, loc, rock, mined.first))

        val empty = resolved.empty ?: return false
        val deplete =
            when (rock.kind) {
                RockKind.Amethyst -> random.of(1, MiningRocks.AMETHYST_DEPLETE_CHANCE) == 1
                else -> true
            }
        if (!deplete) {
            return false
        }
        locRepo.change(loc, empty, rock.respawnTicks)
        resetAnim()
        return true
    }

    /**
     * Rolls the mining gem drop table, which takes the place of the ore roll when it hits.
     * ([Mining](https://oldschool.runescape.wiki/w/Mining#Gems))
     */
    private fun ProtectedAccess.rollRandomGem(): Boolean {
        val chance =
            if (player.isWearingChargedGlory()) {
                MiningRocks.RANDOM_GEM_CHANCE_GLORY
            } else {
                MiningRocks.RANDOM_GEM_CHANCE
            }
        if (random.of(1, chance) != 1) {
            return false
        }
        val gem = rollTable(random, MiningRocks.randomGemTable) ?: return true
        val name = objTypes[gem].name.removePrefix("Uncut ").lowercase()
        invAdd(inv, gem)
        mes("You just found ${article(name)} $name!")
        return true
    }

    private fun ProtectedAccess.essence(): Pair<ObjType, Double> {
        val product = MiningRocks.essence.product
        val obj =
            if (player.miningLvl >= MiningRocks.PURE_ESSENCE_LEVEL) {
                mining_objs.pure_essence
            } else {
                mining_objs.rune_essence
            }
        return obj to product.xp
    }

    private fun ProtectedAccess.gemRock(rock: MiningRock): Pair<ObjType, Double>? {
        val rate = if (player.isWearingChargedGlory()) GLORY_GEM_ROCK_RATE else rock.product.rate
        if (!statRandom(stats.mining, rate.low, rate.high, invisibleLvls)) {
            return null
        }
        val gem = rollTable(random, MiningRocks.gemRockTable) ?: mining_objs.uncut_opal
        return gem to rock.product.xp
    }

    private fun ProtectedAccess.cascade(rock: MiningRock): Pair<ObjType, Double>? {
        for (product in rock.products) {
            if (statRandom(stats.mining, product.rate.low, product.rate.high, invisibleLvls)) {
                return product.obj to product.xp
            }
        }
        return null
    }

    data class ResolvedRock(val type: UnpackedLocType, val rock: MiningRock, val empty: LocType?)

    data class MinedRock(
        val player: Player,
        val loc: BoundLocInfo,
        val rock: MiningRock,
        val product: ObjType,
    ) : UnboundEvent

    companion object {
        private const val MINE_OP = "Mine"

        /** How often the mining animation is restarted while mining. */
        private const val ANIM_CYCLE = 4

        /** Gem rock success rate while wearing a charged amulet of glory (3x the base rate). */
        private val GLORY_GEM_ROCK_RATE = MineRate(84, 210)

        private val chargedGlories: Set<Int> by lazy {
            val objs = mining_objs
            setOf(
                    objs.amulet_of_glory_1,
                    objs.amulet_of_glory_2,
                    objs.amulet_of_glory_3,
                    objs.amulet_of_glory_4,
                    objs.amulet_of_glory_5,
                    objs.amulet_of_glory_6,
                    objs.amulet_of_glory_t1,
                    objs.amulet_of_glory_t2,
                    objs.amulet_of_glory_t3,
                    objs.amulet_of_glory_t4,
                    objs.amulet_of_glory_t5,
                    objs.amulet_of_glory_t6,
                    objs.amulet_of_eternal_glory,
                )
                .mapTo(HashSet()) { it.id }
        }

        fun Player.isWearingChargedGlory(): Boolean = front?.id in chargedGlories

        /**
         * Returns the best pickaxe the player can use, from their worn weapon or inventory. Higher
         * level pickaxes are preferred; ties prefer the worn pickaxe.
         */
        fun findPickaxe(player: Player): Pickaxe? {
            val level = player.miningLvl
            val worn = player.righthand?.let { Pickaxes[it.id] }?.takeIf { level >= it.level }
            val carried =
                player.inv
                    .filterNotNull { Pickaxes[it.id]?.let { pick -> level >= pick.level } == true }
                    .mapNotNull { Pickaxes[it.id] }
                    .maxByOrNull { it.level }
            if (worn != null && carried != null) {
                return if (worn.level >= carried.level) worn else carried
            }
            return worn ?: carried
        }

        fun Pickaxe.rollDelay(random: GameRandom): Int {
            if (fastRollChance > 0 && random.of(1, fastRollChance) == 1) {
                return ticks - 1
            }
            return ticks
        }

        fun rollTable(random: GameRandom, table: List<Pair<ObjType, Int>>): ObjType? {
            var roll = random.of(0, MiningRocks.GEM_TABLE_WEIGHT - 1)
            for ((obj, weight) in table) {
                if (roll < weight) {
                    return obj
                }
                roll -= weight
            }
            return null
        }

        private fun article(name: String): String =
            if (name.isNotEmpty() && name[0].lowercaseChar() in "aeiou") "an" else "a"
    }
}
