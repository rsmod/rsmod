package org.runerealm.api.death

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.runerealm.api.area.checker.WildernessArea
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.components
import org.runerealm.api.config.refs.jingles
import org.runerealm.api.config.refs.midis
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.params
import org.runerealm.api.config.refs.queues
import org.runerealm.api.config.refs.seqs
import org.runerealm.api.config.refs.spotanims
import org.runerealm.api.config.refs.stats
import org.runerealm.api.config.refs.varbits
import org.runerealm.api.config.refs.varns
import org.runerealm.api.config.refs.varps
import org.runerealm.api.death.configs.death_params
import org.runerealm.api.freeze.clearFreeze
import org.runerealm.api.hunt.NpcSearch
import org.runerealm.api.hunt.PlayerSearch
import org.runerealm.api.npc.hit.modifier.NpcHitModifier
import org.runerealm.api.npc.hit.queueHit
import org.runerealm.api.npc.vars.typePlayerUidVarn
import org.runerealm.api.player.deathResetTimers
import org.runerealm.api.player.disablePrayers
import org.runerealm.api.player.hit.queueHit
import org.runerealm.api.player.ironman.HARDCORE_DEATH_MESSAGE
import org.runerealm.api.player.ironman.isHardcoreIronman
import org.runerealm.api.player.ironman.isUltimateIronman
import org.runerealm.api.player.ironman.revokeHardcoreStatus
import org.runerealm.api.player.protect.ProtectedAccess
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.registry.region.RegionRegistry
import org.runerealm.api.repo.obj.ObjRepository
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.hit.HitType
import org.runerealm.game.interact.InteractionOp
import org.runerealm.game.inv.InvObj
import org.runerealm.game.inv.Inventory
import org.runerealm.game.type.hunt.HuntVis
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.stat.StatTypeList
import org.runerealm.game.type.util.UncheckedType
import org.runerealm.map.CoordGrid

@Singleton
public class PlayerDeath
@Inject
constructor(
    private val statTypes: StatTypeList,
    private val objTypes: ObjTypeList,
    private val objRepo: ObjRepository,
    private val values: DeathValues,
    private val gravestones: Gravestones,
    private val playerSearch: PlayerSearch,
    private val npcSearch: NpcSearch,
    private val npcHitModifier: NpcHitModifier,
) {
    private var Player.specialAttackType by intVarp(varps.sa_attack)

    public suspend fun death(access: ProtectedAccess) {
        access.deathSequence()
    }

    private suspend fun ProtectedAccess.deathSequence() {
        activateRetribution()
        val respawn = CoordGrid(0, 50, 50, 21, 18)
        val randomRespawn = mapFindSquareLineOfWalk(respawn, minRadius = 0, maxRadius = 2)
        stopAction()
        delay(2)
        anim(seqs.human_death)
        delay(4)
        combatClearQueue()
        clearQueue(queues.death)
        midiSong(midis.stop_music)
        midiJingle(jingles.death_jingle_2)
        mes("Oh dear, you are dead!")
        // All deaths are currently unsafe; safe minigame deaths do not exist yet.
        if (player.isHardcoreIronman) {
            player.revokeHardcoreStatus()
            mes(HARDCORE_DEATH_MESSAGE)
        }
        processItems(player.coords, respawn)
        telejump(randomRespawn ?: respawn)
        resetAnim()
        resetPlayerState(statTypes)
        restoreToplevelTabs(
            components.toplevel_target_pvp_icons,
            components.toplevel_target_side1,
            components.toplevel_target_side2,
            components.toplevel_target_side4,
            components.toplevel_target_side5,
            components.toplevel_target_side6,
            components.toplevel_target_side9,
            components.toplevel_target_side8,
            components.toplevel_target_side7,
            components.toplevel_target_side10,
            components.toplevel_target_side11,
            components.toplevel_target_side12,
            components.toplevel_target_side13,
        )
    }

    /**
     * [Retribution](https://oldschool.runescape.wiki/w/Retribution): on death, deals up to 25% of
     * the player's base Prayer level (rounded down; it can hit 0) to enemies within one tile. In
     * multi-way combat every adjacent enemy is hit; in single-way combat only the player's opponent
     * (the player who dealt the most damage, or the npc fighting them).
     *
     * Players are only hit where PvP is allowed: both players must be in the Wilderness.
     */
    private fun ProtectedAccess.activateRetribution() {
        if (player.vars[varbits.retribution] == 0) {
            return
        }
        spotanim(spotanims.retribution)
        val maxHit = statBase(stats.prayer) / RETRIBUTION_DIVISOR
        val multi = mapMultiway()
        val hero = findHero()
        val coords = player.coords

        val players =
            playerSearch.findAll(coords, RETRIBUTION_RADIUS, HuntVis.Off).filter {
                it !== player &&
                    it.coords.level == coords.level &&
                    WildernessArea.contains(coords) &&
                    WildernessArea.contains(it.coords) &&
                    (multi || it === hero)
            }
        for (target in players.toList()) {
            target.queueHit(player, delay = 1, HitType.Typeless, random.of(0, maxHit))
        }

        val npcs =
            npcSearch.findAllAny(coords, RETRIBUTION_RADIUS, HuntVis.Off).filter {
                it.coords.level == coords.level &&
                    it.hitpoints > 0 &&
                    it.type.hasOp(InteractionOp.Op2) &&
                    (multi || it.aggressivePlayer == player.uid)
            }
        for (target in npcs.toList()) {
            target.queueHit(player, 1, HitType.Typeless, random.of(0, maxHit), npcHitModifier)
        }
    }

    /**
     * Decides what happens to every carried item, per the wiki's "Items Kept on Death":
     * - Items that are never kept (e.g. looting bag) are deleted; items that are always kept (e.g.
     *   quest items, bonds), and Trouver-locked items on a PvM death, stay with the player.
     * - Of the rest, the 3 most valuable are kept (0 while skulled), plus one with Protect Item
     *   (ultimate ironmen cannot benefit from Protect Item).
     * - A PvP death (killed by a player in the Wilderness) sends lost items to the killer:
     *   tradeables drop as they are (or in their broken/uncharged form), breakable untradeables are
     *   kept by the victim in broken form while the killer gets the repair cost in coins, and other
     *   untradeables go to the victim's grave.
     * - On a PvM death lost items go to a grave (ultimate ironmen drop them on the floor instead).
     */
    @OptIn(UncheckedType::class)
    private fun ProtectedAccess.processItems(deathCoords: CoordGrid, respawn: CoordGrid) {
        val hero = findHero()
        val killer =
            if (hero != null && hero !== player && WildernessArea.contains(deathCoords)) {
                hero
            } else {
                null
            }
        val ultimate = player.isUltimateIronman
        val skulled =
            player.skullIcon == constants.skullicon_default ||
                player.skullIcon == constants.skullicon_red
        var keep = if (skulled) 0 else ITEMS_KEPT_ON_DEATH
        if (player.vars[varbits.protect_item] == 1 && !ultimate) {
            keep++
        }

        val ranked = mutableListOf<Pair<Inventory, Int>>()
        for (inv in listOf(player.inv, player.worn)) {
            for (slot in inv.indices) {
                val obj = inv[slot] ?: continue
                val type = objTypes[obj]
                when {
                    values.isAlwaysDeleted(type) -> inv[slot] = null
                    values.isAlwaysKept(type) -> continue
                    killer == null && type.param(death_params.pvm_keep) -> continue
                    else -> ranked += inv to slot
                }
            }
        }
        // Stable sort: equal values keep inventory order (top-left first), per the wiki.
        ranked.sortByDescending { (inv, slot) -> values.unitValue(checkNotNull(inv[slot])) }

        val lost = mutableListOf<InvObj>()
        for ((inv, slot) in ranked) {
            val obj = checkNotNull(inv[slot])
            val kept = minOf(obj.count, keep)
            keep -= kept
            inv[slot] = if (kept > 0) InvObj(obj.id, kept, obj.vars) else null
            val lostCount = obj.count - kept
            if (lostCount > 0) {
                lost += InvObj(obj.id, lostCount, obj.vars)
            }
        }
        val graveCoords = if (RegionRegistry.inWorkingArea(deathCoords)) respawn else deathCoords
        when {
            killer != null -> dropForKiller(lost, killer, deathCoords, graveCoords)
            ultimate -> dropForUltimateIronman(lost, deathCoords)
            else -> gravestones.bury(player, lost, graveCoords)
        }
        // Bones always drop, for the killer (or the player who died) to see first.
        val boneReceiver = (killer ?: player).takeIf { it.observerUUID != null }
        objRepo.add(objs.bones, deathCoords, constants.lootdrop_duration, boneReceiver)
    }

    private fun ProtectedAccess.dropForKiller(
        lost: List<InvObj>,
        killer: Player,
        deathCoords: CoordGrid,
        graveCoords: CoordGrid,
    ) {
        val aboveLevel20 = WildernessArea.level(deathCoords) > HIGH_RISK_WILDERNESS_LEVEL
        var killerCoins = 0L
        val toGrave = mutableListOf<InvObj>()
        for (obj in lost) {
            val type = objTypes[obj]
            val broken = type.paramOrNull(death_params.pvp_broken)
            val dropAs =
                type.paramOrNull(death_params.pvp_drop_as)
                    ?: type.paramOrNull(params.uncharged_variant)
            when {
                broken != null -> {
                    killerCoins += type.param(death_params.pvp_repair_cost).toLong() * obj.count
                    val lostCompletely = aboveLevel20 && type.param(death_params.lost_above_20)
                    if (!lostCompletely) {
                        val left = gravestones.deposit(player.inv, InvObj(broken, obj.count))
                        if (left != null) {
                            toGrave += left
                        }
                    }
                }
                type.param(death_params.pvp_coins) -> {
                    killerCoins += values.unitValue(type) * obj.count
                }
                dropAs != null -> dropFor(killer, InvObj(dropAs, obj.count), deathCoords)
                values.isTradeable(type) -> dropFor(killer, obj, deathCoords)
                else -> toGrave += obj
            }
        }
        while (killerCoins > 0) {
            val stack = minOf(killerCoins, Int.MAX_VALUE.toLong()).toInt()
            objRepo.add(objs.coins, deathCoords, constants.lootdrop_duration, killer, stack)
            killerCoins -= stack
        }
        gravestones.bury(player, toGrave, graveCoords)
    }

    @OptIn(UncheckedType::class)
    private fun dropFor(receiver: Player, obj: InvObj, coords: CoordGrid) {
        if (objTypes[obj].isStackable || obj.count == 1) {
            objRepo.add(obj, coords, constants.lootdrop_duration, receiver)
            return
        }
        repeat(obj.count) {
            objRepo.add(InvObj(obj.id, 1, obj.vars), coords, constants.lootdrop_duration, receiver)
        }
    }

    /**
     * Ultimate ironmen drop everything they lose on the floor, where they have 60 minutes to pick
     * it back up (wiki: "Items Kept on Death").
     */
    @OptIn(UncheckedType::class)
    private fun ProtectedAccess.dropForUltimateIronman(lost: List<InvObj>, coords: CoordGrid) {
        for (obj in lost) {
            if (objTypes[obj].isStackable || obj.count == 1) {
                objRepo.add(obj, coords, ULTIMATE_IRONMAN_DROP_DURATION, player)
                continue
            }
            repeat(obj.count) {
                objRepo.add(
                    InvObj(obj.id, 1, obj.vars),
                    coords,
                    ULTIMATE_IRONMAN_DROP_DURATION,
                    player,
                )
            }
        }
    }

    private fun ProtectedAccess.resetPlayerState(stats: StatTypeList) {
        player.disablePrayers()
        player.deathResetTimers()
        player.clearFreeze()

        player.specialAttackType = 0
        player.skullIcon = null

        rebuildAppearance()

        camReset()
        statRestoreAll(stats.values)
        minimapReset()
    }

    private companion object {
        const val ITEMS_KEPT_ON_DEATH = 3
        const val HIGH_RISK_WILDERNESS_LEVEL = 20
        const val ULTIMATE_IRONMAN_DROP_DURATION = 6000
        const val RETRIBUTION_DIVISOR = 4
        const val RETRIBUTION_RADIUS = 1
    }
}

private var Npc.aggressivePlayer by typePlayerUidVarn(varns.aggressive_player)
