package org.rsmod.plugins.dev.cmd

import org.rsmod.game.collision.CollisionMap
import org.rsmod.game.collision.buildFlags
import org.rsmod.game.model.client.NpcEntity
import org.rsmod.game.model.item.Item
import org.rsmod.game.model.item.container.transaction.add
import org.rsmod.game.model.item.type.ItemTypeList
import org.rsmod.game.model.map.Coordinates
import org.rsmod.game.model.map.MapSquare
import org.rsmod.game.model.mob.Npc
import org.rsmod.game.model.mob.NpcList
import org.rsmod.game.model.npc.type.NpcTypeList
import org.rsmod.game.model.ui.Component
import org.rsmod.pathfinder.SmartPathFinder
import org.rsmod.plugins.api.cache.type.ui.ComponentTypeList
import org.rsmod.plugins.api.cache.type.ui.InterfaceTypeList
import org.rsmod.plugins.api.model.item.definiteName
import org.rsmod.plugins.api.model.map.toInternalString
import org.rsmod.plugins.api.model.mob.player.inputInt
import org.rsmod.plugins.api.model.mob.player.searchItemCatalogue
import org.rsmod.plugins.api.model.mob.player.sendMessage
import org.rsmod.plugins.api.model.stat.Stats
import org.rsmod.plugins.api.model.ui.closeModal
import org.rsmod.plugins.api.model.ui.openModal
import org.rsmod.plugins.api.onCommand
import org.rsmod.plugins.api.privilege.Privileges
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.system.measureNanoTime

val collision: CollisionMap by inject()
val itemTypes: ItemTypeList by inject()
val npcTypes: NpcTypeList by inject()
val interTypes: InterfaceTypeList by inject()
val compTypes: ComponentTypeList by inject()
val npcList: NpcList by inject()

onCommand("pfbench") {
    privilege = Privileges.Admin
    description = "Benchmark game pathfinding"
    execute {
        val pf = SmartPathFinder()
        val src = player.coords
        val flags = collision.buildFlags(src, pf.searchMapSize)
        val iterations = args.first().toInt()
        var totalNanos = 0L
        var success = 0
        var totalTiles = 0
        repeat(iterations) {
            val dx = ThreadLocalRandom.current().nextInt(-64, 64)
            val dy = ThreadLocalRandom.current().nextInt(-64, 64)
            val dest = src.translate(dx, dy)
            totalNanos += measureNanoTime {
                val route = pf.findPath(flags, src.x, src.y, dest.x, dest.y)
                if (route.success) {
                    success++
                }
                totalTiles += route.size
            }
        }
        val milliseconds = TimeUnit.NANOSECONDS.toMillis(totalNanos)
        val averageTime = milliseconds / iterations.toDouble()
        val averageTiles = totalTiles / iterations
        val iterationMsg = "$iterations iterations"
        val timeMsg = "$averageTime milliseconds per path"
        val pathMsg = "$success successful paths"
        val sizeMsg = "average size $averageTiles"
        player.sendMessage("$iterationMsg, $timeMsg, $pathMsg, $sizeMsg")
    }
}

onCommand("interface") {
    privilege = Privileges.Admin
    description = "Open an interface as a modal"
    execute {
        val id = args.first().toInt()
        val modal = interTypes[id]
        val target = compTypes[Component(548, 22).packed]
        if (player.ui.modals.containsKey(target.toComponent())) {
            player.closeModal(target)
        }
        player.openModal(modal, target)
        player.sendMessage("Opened interface $id")
    }
}

onCommand("npc") {
    privilege = Privileges.Admin
    description = "Spawn npc in current location"
    execute {
        val id = args.first().toInt()
        val type = npcTypes[id]
        val npc = Npc(NpcEntity(), type).apply { coords = player.coords }
        npcList.register(npc)
    }
}

onCommand("item") {
    privilege = Privileges.Admin
    description = "Spawn item in inventory"
    execute {
        fun spawn(item: Item) {
            player.inventory.add(item, strict = false)
            player.sendMessage("Spawned ${item.definiteName(itemTypes)} x ${item.amount} (${item.id})")
        }
        if (args.isEmpty()) {
            player.strongQueue {
                val type = player.searchItemCatalogue("Search item to spawn:")
                val amount = player.inputInt("Enter amount to spawn")
                val item = Item(type, amount)
                spawn(item)
            }
            return@execute
        }
        val id = args.first().toInt()
        val amountStr = if (args.size < 2) "1" else args[1]
            .replace("k", "000")
            .replace("m", "000000")
            .replace("b", "000000000")
        val amount = min(Int.MAX_VALUE.toLong(), amountStr.toLong()).toInt()
        val item = Item(itemTypes[id], amount)
        spawn(item)
    }
}

onCommand("pos") {
    privilege = Privileges.Admin
    description = "Print player coordinates"
    execute {
        player.sendMessage("${player.coords} [${player.coords.toInternalString()}]")
    }
}

onCommand("tele") {
    privilege = Privileges.Admin
    description = "Teleport to given coordinates or map square"
    execute {
        fun teleport(coords: Coordinates) {
            val message = "Displaced to: (${coords.x}, ${coords.y}, ${coords.level}) [${coords.toInternalString()}]"
            player.displace(coords)
            player.sendMessage(message)
        }
        if (args.size == 1) {
            val mapSquare = args.first().toIntOrNull()
            if (mapSquare != null) {
                val coords = MapSquare(mapSquare).coords(player.coords.level)
                teleport(coords)
                return@execute
            }
        }
        val x = args.first().toInt()
        val y = args[1].toInt()
        val level = if (args.size >= 3) args[2].toInt() else player.coords.level
        val newCoords = Coordinates(x, y, level)
        teleport(newCoords)
    }
}

onCommand("master") {
    privilege = Privileges.Admin
    description = "Set max stats"
    execute {
        val lvl = 99
        val xp = Stats.expForLevel(lvl).toDouble()
        Stats.keys.forEach {
            val stat = player.stats.getValue(it)
            stat.currLevel = lvl
            stat.experience = xp
        }
        player.sendMessage("Your stats have been maxed")
    }
}

onCommand("reset") {
    privilege = Privileges.Admin
    description = "Reset stats"
    execute {
        Stats.keys.forEach {
            val lvl = when (it) {
                Stats.Hitpoints -> 10
                else -> 1
            }
            val xp = Stats.expForLevel(lvl).toDouble()
            val stat = player.stats.getValue(it)
            stat.currLevel = lvl
            stat.experience = xp
        }
        player.sendMessage("Your stats have been reset")
    }
}
