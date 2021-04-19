package org.rsmod.plugins.api.update.player.task

import javax.inject.Inject
import org.rsmod.game.model.item.Item
import org.rsmod.game.model.item.container.ItemContainerMap
import org.rsmod.game.model.mob.Player
import org.rsmod.game.model.mob.PlayerList
import org.rsmod.game.model.stat.Stat
import org.rsmod.game.model.stat.StatMap
import org.rsmod.game.model.vars.VarpMap
import org.rsmod.game.update.task.UpdateTask
import org.rsmod.plugins.api.model.mob.player.sendVarp
import org.rsmod.plugins.api.protocol.packet.server.UpdateInvFull
import org.rsmod.plugins.api.protocol.packet.server.UpdateInvPartial
import org.rsmod.plugins.api.protocol.packet.server.UpdateStat

private const val INV_PARTIAL_BYTES_PER_ITEM = Short.SIZE_BYTES + Short.SIZE_BYTES + Byte.SIZE_BYTES
private const val INV_FULL_BYTES_PER_ITEM = Short.SIZE_BYTES + Byte.SIZE_BYTES

class PlayerPostUpdateTask @Inject constructor(
    private val playerList: PlayerList
) : UpdateTask {

    override suspend fun execute() {
        playerList.forEach { player ->
            if (player == null) {
                return@forEach
            }
            player.removeVarps()
            player.updateVarps()
            player.removeContainers()
            player.updateContainers()
            player.updateStats()
            player.entity.updates.clear()
            player.movement.nextSteps.clear()
            player.displace = false
            player.snapshot = player.snapshot()
        }
    }
}

private fun Player.removeContainers(
    oldContainers: ItemContainerMap = snapshot.containers,
    curContainers: ItemContainerMap = containers
) {
    oldContainers.forEach { (key, container) ->
        /* skip containers that do not auto-update */
        if (!container.autoUpdate) {
            return@forEach
        }
        /* if container has been seen previously but is no longer mapped - send it as empty */
        val removed = !curContainers.containsKey(key)
        if (removed) {
            val packet = UpdateInvFull(
                key = key.clientId ?: -1,
                component = key.component?.packed ?: 0,
                items = emptyList()
            )
            write(packet)
        }
    }
}

private fun Player.updateContainers(
    oldContainers: ItemContainerMap = snapshot.containers,
    curContainers: ItemContainerMap = containers
) {
    curContainers.forEach { (key, cur) ->
        /* skip containers that do not auto-update */
        if (!cur.autoUpdate) {
            return@forEach
        }

        /* if container has not been previously sent - send a full update */
        val old = oldContainers[key]
        if (old == null) {
            val packet = UpdateInvFull(
                key = key.clientId ?: -1,
                component = key.component?.packed ?: -1,
                items = cur
            )
            write(packet)
            return@forEach
        }

        /* iterate and compare last-known container with current container */
        val maximumCapacity = old.size.coerceAtLeast(cur.size)
        var updatedItems: MutableMap<Int, Item?>? = null
        repeat(maximumCapacity) { slot ->
            val oldItem = if (slot in old.indices) old[slot] else null
            val curItem = if (slot in cur.indices) cur[slot] else null
            val matchId = oldItem?.id == curItem?.id
            val matchAmount = oldItem?.amount == curItem?.amount
            if (!matchId || !matchAmount) {
                val updated = updatedItems ?: mutableMapOf()
                updated[slot] = curItem
                updatedItems = updated
            }
        }

        /* if container has been updated since last seen - send appropriate update */
        updatedItems?.let { updated ->
            /* only send partial update when there's bandwidth to be saved */
            val partialBytes = updated.size * INV_PARTIAL_BYTES_PER_ITEM
            val fullBytes = cur.size * INV_FULL_BYTES_PER_ITEM
            val packet = if (partialBytes < fullBytes) {
                UpdateInvPartial(
                    key = key.clientId ?: -1,
                    component = key.component?.packed ?: -1,
                    updated = updated
                )
            } else {
                UpdateInvFull(
                    key = key.clientId ?: -1,
                    component = key.component?.packed ?: -1,
                    items = cur
                )
            }
            write(packet)
        }
    }
}

private fun Player.updateStats(
    oldStats: StatMap = snapshot.stats,
    curStats: StatMap = stats
) {
    curStats.forEach { (key, cur) ->
        val old = oldStats[key] ?: Stat.ZERO
        val updateXp = cur.experience != old.experience
        val updateLvl = cur.currLevel != old.currLevel
        if (updateLvl || updateXp) {
            val packet = UpdateStat(
                skill = key.id,
                currLevel = cur.currLevel,
                xp = cur.experience.toInt()
            )
            write(packet)
        }
    }
}

private fun Player.removeVarps(
    oldVarps: VarpMap = snapshot.varps,
    curVarps: VarpMap = varpMap
) {
    oldVarps.forEach { (varp, _) ->
        val removed = !curVarps.containsKey(varp)
        if (removed) {
            sendVarp(varp, 0)
        }
    }
}

private fun Player.updateVarps(
    oldVarps: VarpMap = snapshot.varps,
    curVarps: VarpMap = varpMap
) {
    curVarps.forEach { (varp, cur) ->
        val old = oldVarps[varp] ?: 0
        val update = old != cur
        if (update) {
            sendVarp(varp, cur)
        }
    }
}
