package org.runerealm.api.death

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.runerealm.api.area.checker.WildernessArea
import org.runerealm.api.death.configs.death_invs
import org.runerealm.api.death.configs.death_npcs
import org.runerealm.api.death.configs.death_timers
import org.runerealm.api.death.configs.death_varps
import org.runerealm.api.player.output.mes
import org.runerealm.api.player.vars.intVarp
import org.runerealm.api.player.vars.typeCoordVarp
import org.runerealm.api.repo.npc.NpcRepository
import org.runerealm.game.entity.Npc
import org.runerealm.game.entity.Player
import org.runerealm.game.inv.InvObj
import org.runerealm.game.inv.Inventory
import org.runerealm.game.type.inv.InvTypeList
import org.runerealm.game.type.npc.NpcTypeList
import org.runerealm.game.type.obj.ObjTypeList
import org.runerealm.game.type.util.UncheckedType
import org.runerealm.map.CoordGrid

/**
 * [Graves](https://oldschool.runescape.wiki/w/Grave): items lost on a PvM death (and untradeables
 * lost on a PvP death) are stored in a grave where the player died.
 * - The grave lasts 15 minutes of logged-in time; the timer pauses while the owner is logged out
 *   (the grave is hidden until they return).
 * - Dying again with a grave adds the newly lost items to it and refreshes the timer. A PvM death
 *   in the Wilderness moves an existing grave to where the player died.
 * - Only the owner can loot it, paying the gravestone fee (see [DeathValues.gravestoneFee]).
 * - When the timer runs out the items go to Death's Office, which holds up to 120 items or stacks;
 *   anything beyond that is deleted.
 */
@Singleton
public class Gravestones
@Inject
constructor(
    private val npcTypes: NpcTypeList,
    private val npcRepo: NpcRepository,
    private val invTypes: InvTypeList,
    private val objTypes: ObjTypeList,
) {
    private val graves = HashMap<Long, Npc>()

    private var Player.graveCoords by typeCoordVarp(death_varps.gravestone_coords)
    private var Player.graveTicksLeft by intVarp(death_varps.gravestone_ticks_left)

    public fun gravestoneInv(player: Player): Inventory =
        player.invMap.getOrPut(invTypes[death_invs.gravestone])

    public fun deathOfficeInv(player: Player): Inventory =
        player.invMap.getOrPut(invTypes[death_invs.death_office])

    public fun hasGrave(player: Player): Boolean =
        player.graveTicksLeft > 0 && gravestoneInv(player).isNotEmpty()

    public fun graveNpc(player: Player): Npc? = graves[player.ownerKey]

    public fun graveCoords(player: Player): CoordGrid? =
        if (hasGrave(player)) player.graveCoords else null

    public fun ticksLeft(player: Player): Int = player.graveTicksLeft

    /** Returns `true` if [npc] is [player]'s grave. */
    public fun isOwner(player: Player, npc: Npc): Boolean = graves[player.ownerKey] === npc

    /** Returns `true` if [npc] is a grave spawned by this service. */
    public fun isGrave(npc: Npc): Boolean = graves.values.any { it === npc }

    /**
     * Stores [lost] in [player]'s grave at [coords] (or in their existing grave), and starts or
     * refreshes its 15-minute timer.
     */
    public fun bury(player: Player, lost: List<InvObj>, coords: CoordGrid) {
        if (lost.isEmpty()) {
            return
        }
        val grave = gravestoneInv(player)
        val existing = hasGrave(player)
        if (!existing) {
            // A stale, expired grave inventory should never linger, but make sure.
            moveToDeathOffice(player)
        }
        var overflow = false
        for (obj in lost) {
            val left = deposit(grave, obj) ?: continue
            overflow = true
            deposit(deathOfficeInv(player), left)
        }
        val moveGrave = !existing || WildernessArea.contains(coords)
        val graveCoords = if (moveGrave) coords else player.graveCoords ?: coords
        player.graveCoords = graveCoords
        player.graveTicksLeft = GRAVESTONE_DURATION
        spawn(player)
        if (existing) {
            player.mes("Some of your items have been added to your previous gravestone.")
        } else {
            player.mes(
                "Your items will be held in a gravestone where you died. You have " +
                    "${GRAVESTONE_DURATION / TICKS_PER_MINUTE} minutes to collect them."
            )
        }
        if (overflow) {
            player.mes("Some items have been moved to Death's Office from your gravestone.")
        }
    }

    /** Spawns (or moves) [player]'s grave npc. Used on death and on login. */
    public fun spawn(player: Player) {
        val coords = player.graveCoords ?: return
        if (!hasGrave(player)) {
            return
        }
        val existing = graves[player.ownerKey]
        if (existing != null && existing.coords == coords) {
            player.softTimer(death_timers.gravestone, TIMER_INTERVAL)
            return
        }
        despawn(player)
        val npc = Npc(npcTypes[death_npcs.gravestone], coords)
        npcRepo.add(npc, Int.MAX_VALUE)
        graves[player.ownerKey] = npc
        player.softTimer(death_timers.gravestone, TIMER_INTERVAL)
    }

    /** Removes [player]'s grave npc without touching its contents (e.g. on logout). */
    public fun despawn(player: Player) {
        val npc = graves.remove(player.ownerKey) ?: return
        npcRepo.del(npc, Int.MAX_VALUE)
    }

    /** Hides the grave and pauses its timer while [player] is logged out. */
    public fun logout(player: Player) {
        despawn(player)
        player.clearSoftTimer(death_timers.gravestone)
    }

    /** Counts the grave timer down; called every [TIMER_INTERVAL] ticks while logged in. */
    public fun tick(player: Player) {
        if (!hasGrave(player)) {
            remove(player)
            return
        }
        val left = player.graveTicksLeft - TIMER_INTERVAL
        if (left > 0) {
            player.graveTicksLeft = left
            if (left == WARNING_TICKS) {
                player.mes("Your gravestone will collapse in one minute.")
            }
            return
        }
        expire(player)
    }

    /** The grave has run out of time: its items go to Death's Office. */
    public fun expire(player: Player) {
        val deleted = moveToDeathOffice(player)
        remove(player)
        player.mes(
            "Your gravestone has collapsed. Your items can be reclaimed from Death in his office."
        )
        if (deleted) {
            player.mes("Death's Office was full, so some of your items have been lost.")
        }
    }

    /**
     * Moves everything in [player]'s grave to Death's Office. Returns `true` if the office was full
     * and some items were deleted.
     */
    public fun moveToDeathOffice(player: Player): Boolean {
        val grave = gravestoneInv(player)
        val office = deathOfficeInv(player)
        var deleted = false
        for (slot in grave.indices) {
            val obj = grave[slot] ?: continue
            grave[slot] = null
            if (deposit(office, obj) != null) {
                deleted = true
            }
        }
        return deleted
    }

    /** Clears the grave npc, timer and varps once the grave is empty or has expired. */
    public fun remove(player: Player) {
        despawn(player)
        player.clearSoftTimer(death_timers.gravestone)
        player.graveTicksLeft = 0
        player.graveCoords = null
    }

    /**
     * Adds [obj] to [inv], merging stackable objs into an existing stack. Returns whatever did not
     * fit, or `null` if everything was stored.
     */
    @OptIn(UncheckedType::class)
    public fun deposit(inv: Inventory, obj: InvObj): InvObj? {
        val type = objTypes[obj]
        if (type.isStackable) {
            for (slot in inv.indices) {
                val existing = inv[slot] ?: continue
                if (existing.id != obj.id || existing.vars != obj.vars) {
                    continue
                }
                val total = existing.count.toLong() + obj.count
                val stored = minOf(total, Int.MAX_VALUE.toLong()).toInt()
                inv[slot] = InvObj(obj.id, stored, obj.vars)
                val left = (total - stored).toInt()
                return if (left > 0) InvObj(obj.id, left, obj.vars) else null
            }
            val free = inv.indexOfFirst { it == null }
            if (free == -1) {
                return obj
            }
            inv[free] = obj
            return null
        }
        var remaining = obj.count
        while (remaining > 0) {
            val free = inv.indexOfFirst { it == null }
            if (free == -1) {
                return InvObj(obj.id, remaining, obj.vars)
            }
            inv[free] = InvObj(obj.id, 1, obj.vars)
            remaining--
        }
        return null
    }

    private val Player.ownerKey: Long
        get() = checkNotNull(uuid) { "Player has no uuid: $this" }

    public companion object {
        public const val TICKS_PER_MINUTE: Int = 100
        public const val GRAVESTONE_DURATION: Int = 15 * TICKS_PER_MINUTE
        public const val TIMER_INTERVAL: Int = 1
        private const val WARNING_TICKS = TICKS_PER_MINUTE
    }
}
