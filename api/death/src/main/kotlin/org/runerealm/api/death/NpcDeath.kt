package org.runerealm.api.death

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.runerealm.api.config.constants
import org.runerealm.api.config.refs.objs
import org.runerealm.api.config.refs.params
import org.runerealm.api.config.refs.varns
import org.runerealm.api.config.refs.varps
import org.runerealm.api.drop.table.NpcDropTables
import org.runerealm.api.freeze.clearFreeze
import org.runerealm.api.npc.access.StandardNpcAccess
import org.runerealm.api.npc.vars.typePlayerUidVarn
import org.runerealm.api.player.ironman.isIronman
import org.runerealm.api.player.output.soundSynth
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.player.vars.typeNpcUidVarp
import org.runerealm.api.random.GameRandom
import org.runerealm.api.repo.npc.NpcRepository
import org.runerealm.api.repo.obj.ObjRepository
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.entity.PlayerList
import org.runerealm.game.entity.npc.NpcUid
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.seq.SeqTypeList
import org.runerealm.map.CoordGrid

@Singleton
public class NpcDeath
@Inject
constructor(
    private val npcRepo: NpcRepository,
    private val seqTypes: SeqTypeList,
    private val players: PlayerList,
    private val objRepo: ObjRepository,
    private val objTypes: ObjTypeList,
    private val dropTables: NpcDropTables,
    private val random: GameRandom,
) {
    public suspend fun deathNoDrops(access: StandardNpcAccess) {
        access.death(npcRepo, seqTypes, players)
    }

    public suspend fun deathWithDrops(
        access: StandardNpcAccess,
        dropCoords: CoordGrid = access.coords,
    ) {
        access.death(npcRepo, seqTypes, players)
        access.npc.spawnDeathDrops(dropCoords)
    }

    private fun Npc.spawnDeathDrops(dropCoords: CoordGrid) {
        val hero = findHero(players) ?: return
        if (hero.isIronman && heroPoints.toMutableList().any { it.uuid != hero.uuid }) {
            // Ironmen get no loot whatsoever if another player has attacked the npc.
            return
        }
        val duration = hero.lootDropDuration ?: constants.lootdrop_duration

        val table = dropTables[type]
        if (table == null) {
            // TODO: Remove fallback once every npc has its drop table defined.
            objRepo.add(objs.bones, dropCoords, duration, hero)
            return
        }

        for ((obj, count) in dropTables.roll(table, random)) {
            if (objTypes[obj].stackable) {
                objRepo.add(obj, dropCoords, duration, hero, count)
                continue
            }
            repeat(count) { objRepo.add(obj, dropCoords, duration, hero) }
        }
    }

    // Note: We may be able to have `Npc` as the arg instead of `StandardNpcAccess`, however we
    // will need to wait and see how [spawnDeathDrops] ends up once it handles everything it needs
    // to.
    public fun spawnDrops(access: StandardNpcAccess, dropCoords: CoordGrid = access.coords) {
        access.npc.spawnDeathDrops(dropCoords)
    }
}

private var Player.lastCombat: Int by intVarp(varps.lastcombat)
private var Player.aggressiveNpc: NpcUid? by typeNpcUidVarp(varps.aggressive_npc)
private var Npc.aggressivePlayer by typePlayerUidVarn(varns.aggressive_player)

/**
 * Handles the death sequence of this [StandardNpcAccess.npc], including clearing interactions and
 * removing (or hiding, if it respawns) the npc from the world.
 *
 * **Notes:**
 * - This is **not** the way to "kill" a npc. This "death sequence" occurs after the npc has already
 *   been deemed dead and its death queue is being processed.
 * - To queue a npc's death, use [StandardNpcAccess.queueDeath] or
 *   [org.runerealm.api.npc.queueDeath] instead.
 * - This function **does not** spawn any drop table objs for the npc.
 * - Drop table spawns are handled via [NpcDeath.deathWithDrops], which is **automatically called**
 *   for queued deaths by default. However, if you override death queues for specific npc types
 *   (`onNpcQueue(npc_type, queues.death)`), you must explicitly handle drop spawns in the script by
 *   injecting `NpcDeath` and calling either [NpcDeath.deathWithDrops] or [NpcDeath.spawnDrops].
 */
public suspend fun StandardNpcAccess.death(
    npcRepo: NpcRepository,
    seqTypes: SeqTypeList,
    players: PlayerList,
) {
    walk(coords)
    noneMode()
    hideAllOps()
    arriveDelay()

    val aggressivePlayer = npc.aggressivePlayer
    if (aggressivePlayer != null) {
        val player = aggressivePlayer.resolve(players)

        val deathSound = paramOrNull(params.death_sound)
        if (deathSound != null && player != null) {
            player.soundSynth(deathSound)
        }

        // TODO(combat): Should we assert that npc.uid will always match player.aggressiveNpc at
        // this point?

        if (player != null && player.aggressiveNpc == npc.uid) {
            player.lastCombat = 0
        }
    }

    val deathAnim = param(params.death_anim)
    anim(deathAnim)
    delay(seqTypes[deathAnim])

    npc.clearFreeze()

    if (npc.respawns) {
        npcRepo.despawn(npc, npc.type.respawnRate)
        return
    }

    npcRepo.del(npc, Int.MAX_VALUE)
}
